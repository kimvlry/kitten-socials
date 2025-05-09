# 🐾 KittenSocials: A Social Network for Cats

Welcome to **KittenSocials** — the most adorable social network you'll ever find, built not for people, 
but for their murziks. 
Owners can register their kittens, set their breed and purring loudness, and — most importantly — help them 
make friends with other kittens. Because even introverted tabbies need some sort of social life. 
**No ugly brainrot there, just meow-meow.**

## Features
- **Create kitten friendships** across different owners.
- **Advanced kitten search** with **filtering** on:
    - name
    - breed
    - coat color
    - purr loudness!
    - birth date
- **Secure backend**:
    - passwords are stored encrypted.
    - stateless authentication via **JWT tokens**.
    - secured endpoints: your kitty’s personal page is protected — only you (~~and admin~~) can access it, thanks to `Spring Security` and **role-based** access control.

---

## Quick start
### 🔧 ENVs

To run the project, you need to define the following **environment variables**:

| Variable               | Description                                |
|------------------------|--------------------------------------------|
| `JWT_SECRET`           | secret key used to **sign** and **verify** JWTs    |
| `JWT_ACCESS_EXPIRATION`| access token **lifetime** in milliseconds  |
| `JWT_REFRESH_EXPIRATION`| refresh token **lifetime** in milliseconds |

run smth like this in your supercool terminal:
```bash
  export JWT_SECRET=your_super_secret_key
  export JWT_ACCESS_EXPIRATION=300000   
  export JWT_REFRESH_EXPIRATION=1209600000 
```

dopishu popozhe...

---
## Stack

|               |                              |
|---------------|------------------------------|
| Java standard | 21                           |
| Framework     | Spring Boot                  |
| Build Tool    | Gradle                       |
| Database      | PostgreSQL                   |
| Persistence   | Spring Data JPA + Hibernate  |
| DB migrations | Flyway                       |
| Security      | Spring Security + JWT        |
| Testing       | JUnit + Mockito              |
| Documentation | Swagger (OpenAPI 3), Javadoc |
