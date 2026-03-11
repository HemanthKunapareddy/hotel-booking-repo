# 🏨 AirBnB Hotel Booking System

A backend for a hotel booking platform built with Spring Boot. It covers the full booking lifecycle — from hotel onboarding to room availability, guest management, and payment processing via Stripe. JWT-based auth is baked in, with role-based access control for hotel managers and guests.

---

## 📑 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Module Breakdown](#module-breakdown)
- [API Reference](#api-reference)
- [Security](#security)
- [Pricing Engine](#pricing-engine)
- [Inventory Management](#inventory-management)
- [Payment Integration](#payment-integration)
- [Scheduled Jobs](#scheduled-jobs)
- [Domain Model](#domain-model)
- [Error Handling](#error-handling)
- [Project Structure](#project-structure)
- [Known Issues & Improvements](#known-issues--improvements)

---

## Overview

This application serves as the backend for a hotel booking platform. It supports two primary roles:

- **Hotel Manager (`HOTEL_MANAGER`)** — Can onboard hotels, manage rooms, assign roles to users.
- **Guest (`GUEST`)** — Can browse hotels, make bookings, add guests, and pay via Stripe.

The booking lifecycle follows a strict state machine:

```
RESERVED → GUESTS_ADDED → PAYMENTS_PENDING → CONFIRMED
                                           ↘ CANCELLED / EXPIRED
```

Bookings have a **10-minute reservation window**. If the guest does not complete payment within this window, the booking is treated as expired and inventory is automatically freed.

---

## Tech Stack

| Layer              | Technology                              |
|--------------------|-----------------------------------------|
| Framework          | Spring Boot 3.x                         |
| Security           | Spring Security + JWT (JJWT)            |
| Persistence        | Spring Data JPA + Hibernate             |
| Database           | PostgreSQL (arrays via `TEXT[]`)        |
| Payments           | Stripe SDK (Checkout Sessions + Webhooks) |
| API Documentation  | SpringDoc OpenAPI 3 (Swagger UI)        |
| Object Mapping     | ModelMapper                             |
| Scheduling         | Spring `@Scheduled`                     |
| Build Tool         | Maven                                   |
| Boilerplate        | Lombok                                  |

---

## Architecture

The application follows a classic **Layered Architecture**:

```
Controller Layer      →  Handles HTTP requests/responses, input validation
Service Layer         →  Business logic, orchestration
Repository Layer      →  Database access via Spring Data JPA
Entity / Domain Layer →  JPA entities, enums
DTO Layer             →  Data Transfer Objects for API I/O
Security Layer        →  JWT filter, AuthService, UserDetailsService
Strategy Layer        →  Pluggable pricing calculation strategies
```

There is a clear **separation of concerns** — controllers never touch repositories directly, and entities are never exposed in API responses (DTOs are used throughout).

---

## Design Patterns

This project consciously applies several industry-standard design patterns:

### 1. 🎭 Decorator Pattern — Pricing Engine

The most architecturally notable pattern in this project. The pricing engine chains multiple pricing strategies using the Decorator pattern:

```
BasePricingStrategy
    └── wrapped by SurgePricingStrategy
            └── wrapped by HolidayPricingStrategy
                    └── wrapped by OccupancyPricingStrategy
                            └── wrapped by UrgencyPricingStrategy
```

Each decorator wraps the previous one, applies its own pricing rule, and delegates to the inner strategy. This makes adding or removing pricing rules as simple as adding/removing a wrapper — no existing code needs to change (Open/Closed Principle).

```java
PricingStrategy pricingStrategy = new BasePricingStrategy();
pricingStrategy = new SurgePricingStrategy(pricingStrategy);
pricingStrategy = new HolidayPricingStrategy(pricingStrategy);
pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
```

### 2. 📐 Strategy Pattern — Pricing Interface

`PricingStrategy` is a clean interface with a single method `calculateFinalPrice(Inventory inventory)`. Each concrete strategy implements this contract independently, making strategies interchangeable and testable in isolation.

### 3. 🏗️ Builder Pattern — Entity Construction

Entities like `Booking` and `Inventory` use Lombok's `@Builder` for readable, safe object construction — especially useful when creating complex entities with many optional fields.

### 4. 🔗 Chain of Responsibility — Security Filter Chain

`JwtAuthFilter` extends `OncePerRequestFilter` and participates in Spring Security's filter chain. It validates the JWT token, extracts the user, and sets the `SecurityContext` — all before the request reaches the controller.

### 5. 📦 Repository Pattern

All database interactions are abstracted behind Spring Data JPA repository interfaces. Custom JPQL queries are written inside the repositories using `@Query`, keeping business logic out of persistence code.

### 6. 🏭 Service Layer Pattern (Facade)

Service classes like `BookingServiceImpl` act as facades orchestrating multiple repositories, utility classes, and external services (Stripe) into a single cohesive business operation.

---

## Module Breakdown

### Auth Module (`/Auth`)
Handles user registration, login, and JWT token refresh. On login, an access token (short-lived) and a refresh token are issued. The refresh token is stored in an `HttpOnly` cookie for security.

### Hotel Management Module (`/admin/hotels`)
HOTEL_MANAGER secured. Supports full CRUD on hotels. When a hotel is set to **active**, inventory records for all its rooms are automatically created for the next 365 days.

### Room Management Module (`/admin/{hotelId}/rooms`)
HOTEL_MANAGER secured. Manages rooms under a hotel. If the hotel is already active when a new room is added, inventory is created immediately for that room as well.

### Hotel Browsing Module (`/browseHotel`)
Public-facing. Allows guests to search hotels by city and date range with availability filtering. Also supports searching hotels by minimum price per date range.

### Booking Module (`/booking`)
Authenticated. Manages the full booking lifecycle — initialization (inventory reservation), guest addition, payment initiation, and payment cancellation with Stripe refund.

### Role Assignment Module (`/admin/assignRoles`)
HOTEL_MANAGER secured. Allows assigning additional roles to existing users.

### Webhook Module (`/webhook`)
Receives Stripe webhook events. On `checkout.session.completed`, booking status is confirmed and inventory is updated from `reservedCount` to `bookedCount`.

---

## API Reference

### Auth

| Method | Endpoint         | Description                    | Auth Required |
|--------|------------------|--------------------------------|---------------|
| POST   | `/Auth/signUp`   | Register a new user            | No            |
| POST   | `/Auth/login`    | Login, receive JWT tokens      | No            |
| GET    | `/Auth/refresh`  | Refresh access token via cookie| No            |

### Hotel Management

| Method | Endpoint                    | Description                        | Role           |
|--------|-----------------------------|------------------------------------|----------------|
| POST   | `/admin/hotels`             | Create a new hotel                 | HOTEL_MANAGER  |
| GET    | `/admin/hotels`             | Get all hotels                     | HOTEL_MANAGER  |
| GET    | `/admin/hotels/{hotelId}`   | Get hotel by ID                    | HOTEL_MANAGER  |
| PUT    | `/admin/hotels/{hotelId}`   | Update hotel details               | HOTEL_MANAGER  |
| PATCH  | `/admin/hotels/{hotelId}`   | Set hotel to active (creates inventory) | HOTEL_MANAGER |
| DELETE | `/admin/hotels/{hotelId}`   | Delete a hotel                     | HOTEL_MANAGER  |

### Room Management

| Method | Endpoint                               | Description             | Role           |
|--------|----------------------------------------|-------------------------|----------------|
| POST   | `/admin/{hotelId}/rooms`               | Add a room to a hotel   | HOTEL_MANAGER  |
| GET    | `/admin/{hotelId}/rooms`               | Get all rooms in hotel  | HOTEL_MANAGER  |
| GET    | `/admin/{hotelId}/rooms/{roomId}`      | Get a specific room     | HOTEL_MANAGER  |
| DELETE | `/admin/{hotelId}/rooms/{roomId}`      | Delete a room           | HOTEL_MANAGER  |

### Hotel Browsing

| Method | Endpoint                                   | Description                                   | Auth Required |
|--------|--------------------------------------------|-----------------------------------------------|---------------|
| POST   | `/browseHotel/search`                      | Search hotels by city & date availability     | No            |
| POST   | `/browseHotel/searchHotelBasedOnMinPrice`  | Search hotels with minimum price per date     | No            |
| GET    | `/browseHotel/{hotelId}`                   | Get hotel details with rooms                  | No            |

### Booking

| Method | Endpoint                              | Description                          | Auth Required |
|--------|---------------------------------------|--------------------------------------|---------------|
| POST   | `/booking/initialize`                 | Initialize a booking (reserve rooms) | Yes           |
| POST   | `/booking/{bookingId}/addGuests`      | Add guests to a booking              | Yes           |
| POST   | `/booking/{bookingId}/initializePayment` | Get Stripe checkout session URL   | Yes           |
| POST   | `/booking/{bookingId}/cancelPayment`  | Cancel booking and trigger refund    | Yes           |

### Admin

| Method | Endpoint               | Description              | Role           |
|--------|------------------------|--------------------------|----------------|
| POST   | `/admin/assignRoles`   | Assign roles to a user   | HOTEL_MANAGER  |

### Webhook

| Method | Endpoint              | Description                        | Auth Required |
|--------|-----------------------|------------------------------------|---------------|
| POST   | `/webhook/payments`   | Stripe webhook for payment capture | No (Stripe-signed) |

---

## Security

### JWT Authentication Flow

1. User logs in via `/Auth/login`.
2. Server issues:
    - **Access Token** — short-lived (5 minutes), returned in response body.
    - **Refresh Token** — returned in response body and also set as an `HttpOnly` cookie.
3. Client sends `Authorization: Bearer <accessToken>` on subsequent requests.
4. `JwtAuthFilter` intercepts every request (except `/Auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`), validates the token, and sets the Spring `SecurityContext`.
5. When the access token expires, the client calls `/Auth/refresh` — the refresh token is read from the `HttpOnly` cookie and a new access token is issued.

### Role-Based Access Control

Spring Security's `@PreAuthorize` and `.hasRole()` matchers are used:

- `/admin/**` → Requires `HOTEL_MANAGER` role.
- `/booking/**` → Requires any authenticated user.
- `/browseHotel/**`, `/Auth/**`, Swagger endpoints → Public.

### Exception Delegation in Filters

Both `JwtAuthFilter` and the `AccessDeniedHandler` delegate exceptions to Spring's `HandlerExceptionResolver`. This ensures all security errors go through `GlobalExceptionHandler` and return proper JSON error responses — instead of Spring's default HTML error pages.

> ⚠️ **Note:** Both access token and refresh token currently have the same expiry (5 minutes). In production, the refresh token should have a significantly longer TTL (e.g., 7–30 days).

---

## Pricing Engine

The pricing engine calculates the final price for each inventory slot using a chain of decorators:

| Strategy                  | Rule Applied                                                           |
|---------------------------|------------------------------------------------------------------------|
| `BasePricingStrategy`     | Returns room's `basePrice` as the starting price                       |
| `SurgePricingStrategy`    | Multiplies by `surgeFactor` stored in inventory                        |
| `HolidayPricingStrategy`  | Applies 1.5× multiplier on holidays (`isHoliday` currently hardcoded) |
| `OccupancyPricingStrategy`| Applies 1.2× if occupancy > 80%                                        |
| `UrgencyPricingStrategy`  | Applies 1.5× if check-in is within 7 days                             |

`PricingStrategyService` computes the final price per inventory slot and sums them up across all days for a booking:

```java
BigDecimal totalPrice = pricingStrategyService.calculatePrice(inventories)
                            .multiply(BigDecimal.valueOf(roomsCount));
```

> 💡 **Note:** `HolidayPricingStrategy` has `isHoliday = true` hardcoded. This is a placeholder for a real holiday calendar integration (e.g., a public holidays API or a configurable list).

---

## Inventory Management

When a hotel is set to **active**, the system creates `Inventory` records for every room for the **next 365 days**. Each `Inventory` record represents:

- A specific **room** on a specific **date**
- `totalCount` — total rooms available (default: 10)
- `bookedCount` — confirmed bookings
- `reservedCount` — rooms held during active booking sessions (released if payment is not completed)
- `price` — dynamically calculated price
- `surgeFactor` — used by the surge pricing strategy
- `closed` — flag to mark a date as unavailable

A **unique constraint** on `(hotel_id, room_id, date)` prevents duplicate inventory records.

### Concurrency & Locking

`PESSIMISTIC_WRITE` locks (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) are applied on inventory queries during:
- **Booking initialization** — to safely increment `reservedCount`
- **Payment confirmation** — to safely convert `reservedCount` to `bookedCount`
- **Cancellation** — to safely decrement `bookedCount`

This prevents race conditions when multiple users try to book the same room on the same date simultaneously.

---

## Payment Integration

Stripe Checkout is used for payment processing:

1. **`/booking/{bookingId}/initializePayment`** — Creates a Stripe `Customer` and a `Session`, stores the `sessionId` in the booking, and returns the Stripe-hosted checkout URL.
2. Guest completes payment on Stripe's hosted page.
3. Stripe calls **`/webhook/payments`** with a signed event payload.
4. The webhook verifies the Stripe signature using `endpointSecret`, then calls `bookingService.capturePayment(event)`.
5. On `checkout.session.completed`, booking status becomes `CONFIRMED` and inventory is updated.

### Cancellation & Refund

Calling `/booking/{bookingId}/cancelPayment` on a `CONFIRMED` booking:
- Sets status to `CANCELLED`
- Retrieves the Stripe `Session`, extracts `paymentIntentId`, and creates a `Refund` via Stripe API
- Decrements `bookedCount` in inventory

---

## Scheduled Jobs

`PricingUpdateService` runs a scheduled job **every hour** (`cron = "0 0 * * * *"`):

1. Fetches all hotels in paginated batches (100 per page).
2. For each hotel, loads inventory for the next 365 days.
3. Recalculates prices using `PricingStrategyService` and updates `Inventory.price`.
4. Updates `HotelMinPrice` records — which store the minimum price per hotel per date, used by the hotel browsing search for efficient price-range queries.

`@EnableScheduling` is configured on the main application class.

---

## Domain Model

```
User
 ├── roles: Set<Role>  [GUEST, HOTEL_MANAGER]
 └── implements UserDetails

Hotel
 ├── owner: User
 ├── contactInfo: ContactInfo (Embeddable)
 ├── rooms: List<Room>
 └── active: boolean

Room
 └── hotel: Hotel

Inventory
 ├── hotel: Hotel
 ├── room: Room
 ├── date: LocalDate
 ├── bookedCount, reservedCount, totalCount
 ├── price, surgeFactor
 └── closed: boolean

Booking
 ├── hotelId: Hotel
 ├── roomId: Room
 ├── userId: User
 ├── guests: Set<Guest>
 ├── bookingStatus: BookingStatus
 ├── amount: BigDecimal
 └── paymentSessionId: String

Guest
 └── userId: User

Payment
 └── bookingId: Booking

HotelMinPrice
 ├── hotel: Hotel
 ├── date: LocalDate
 └── minPrice: BigDecimal
```

### Booking Status Enum

```
RESERVED → GUESTS_ADDED → PAYMENTS_PENDING → CONFIRMED → CANCELLED
                                                        → EXPIRED
NOT_BOOKED (unused / initial state)
```

---

## Error Handling

All exceptions are handled centrally via `GlobalExceptionHandler` (`@RestControllerAdvice`):

| Exception                  | HTTP Status           |
|----------------------------|-----------------------|
| `ResourceNotFoundException`| 404 Not Found         |
| `JwtException`             | 401 Unauthorized      |
| `AccessDeniedException`    | 403 Forbidden         |
| `Exception` (catch-all)    | 500 Internal Server Error |

All errors return a consistent `ApiError` JSON response:

```json
{
  "timeStamp": "2025-03-10T10:30:00",
  "statusCode": "NOT_FOUND",
  "message": "Hotel with Id: 5 not found"
}
```

---

## Project Structure

```
com.hotelBooking.airBnB
├── config/                   # AppConfig, OpenAPIConfig, StripeConfig
├── constants/                # AppConstants
├── controller/               # REST Controllers
├── dto/                      # Data Transfer Objects
├── entity/                   # JPA Entities
├── enums/                    # BookingStatus, Gender, PaymentStatus, Role
├── exceptions/               # Custom exceptions
├── handler/                  # GlobalExceptionHandler
├── repository/               # Spring Data JPA Repositories
├── security/
│   ├── config/               # SecurityConfig
│   ├── filter/               # JwtAuthFilter
│   └── service/              # AuthService, JwtService, MyUserService
├── service/
│   ├── implementation/       # Service implementations
│   └── (interfaces)          # Service contracts
├── strategy/                 # Pricing strategy classes
└── util/                     # UserUtil
```
---

## API Documentation

Swagger UI is available (when the application is running) at:

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON spec at:

```
http://localhost:8080/v3/api-docs
```

---

*Built with Spring Boot · Secured with JWT · Payments via Stripe*
