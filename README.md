# multi-module-springboot-boilerplate
multi-module-springboot-boilerplate

1. Hexagonal Architecure.
```
       ┌──────────┐
       │   API    │  ← HTTP, CLI, gRPC
       └────┬─────┘
            ↓
       ┌──────────┐
       │Application│
       └────┬─────┘
            ↓
       ┌──────────┐
       │  Domain  │
       └────┬─────┘
            ↑
       ┌──────────┐
       │ Infra    │  ← DB, MQ, REST
       └──────────┘
```
```
bootstrap-spring
 └── depends on infra only

infra
 ├── depends on application
 └── depends on domain

api
 └── depends on application
 ```

2️⃣ What is Application (in Hexagonal / Clean Architecture)?
Application = Use-case layer

It answers the question:

“What can this system do?”

Responsibilities

Orchestrates business use cases

Coordinates domain objects

Defines ports (interfaces) for external systems

Controls transactions and consistency

Has zero knowledge of frameworks

Example responsibilities

Create something

Process something

Approve / reject something

Fetch something in a business-meaningful way

What lives here
```
application
 ├── usecase
 │    └── CreateXxxUseCase
 ├── port
 │    ├── InboundPort
 │    └── OutboundPort
 └── service
      └── XxxApplicationService
```
What it MUST NOT contain

❌ @SpringBootApplication
❌ Controllers
❌ Repositories implementations
❌ Framework annotations

2.1 Application layer is too thin (currently empty shell)

Your application module is almost empty.

That’s fine for now, but the next code must go here, not in api or infra.

What should live in application

Use cases

Ports (interfaces)

Transaction boundaries

Orchestration logic

If you skip this, you’ll slide into controller-driven logic (very common mistake).

❌ 3️⃣ API module risks becoming a “smart controller”

Your controllers are currently fine, but:

⚠️ If you add:

business rules

decision logic

database calls

into controllers → architecture breaks.

Controllers must:

validate input

call application use case

map output

Nothing more.

3️⃣ What is bootstrap-spring (Why it Exists)
bootstrap-spring = Runtime entry point

It answers the question:

“How do we start this system?”

Responsibilities

Contains main() method

Boots Spring

Wires adapters together

Starts embedded server

Loads configuration

What lives here
```
bootstrap-spring
 ├── SpringDemoApplication.java
 └── config
     └── AppConfig.java
```
What it MUST NOT contain

❌ Business logic
❌ Use cases
❌ Domain rules

4️⃣ Analogy (Very Important)
🎮 Game Analogy
Concept	Real Life Equivalent
Domain	Game rules
Application	Game flow (turns, win conditions)
API	Controller / Keyboard
Infrastructure	Graphics / Sound / Storage
Bootstrap-spring	Game launcher (.exe)

The launcher is not the game logic.

5️⃣ Mapping This to YOUR Project

Your current structure:
```
springdemo-root
├── domain            ← WHAT the business is
├── application       ← WHAT the system can do
├── api               ← HOW users talk to it
├── infra             ← HOW system talks outward
└── bootstrap-spring  ← HOW the system starts
```
Concrete meaning in your repo
Module	Purpose
domain	Business rules, entities
application	Use cases, orchestration
api	REST controllers
infra	DB / external implementations
bootstrap-spring Spring Boot entry point
```
bootstrap-spring
 └── infra
       └── application
             └── domain
api
 └── application
       └── domain
```
🔚 Final Takeaway

Application = brain

Domain = rules

API = mouth

Infra = hands

Bootstrap-spring = body switch-on button