# Service composition with Identity Provider for authentication
## What do we see here?

## How can I use it?
- Adapt `.env` and set `HOST_IP` to your hosts primary IP address
- Run `docker compose up` (that should build the application and run all components)
- There are 3 users preconfigured (password == username) with different roles:
  - microservices-guest
  - microservices-user
  - microservices-admin
- You can access the Keycloak admin console at `http://localhost:8090/` (credentials for master realm: `admin` and `admin`)
### spring-boot-redirect
- Point your browser at `http://localhost:8080/`, you should see a Keycloak login screen
  - Login and see what you can do...
### spring-boot-resource-server
- Point your browser at `http://localhost:8081/`, you should see a simple HTML webpage
  - You can retrieve a JWT token for the users mentioned above by entering their credentials
  - The frontend will then print and store that token and use it for any request you can trigger through the request buttons
  - This is a simplified version of the frontend token retrieval process for educational purposes! It is by no means secure or production-ready. In real-life there would be a frontend library taking care of redirecting you to Keycloak (as with the other example) and parsing the token that is then passed via the return URL
  - Use jwt.io to introspect a token (Never copy actual production tokens into that or any other webpage! It should be fine on jwt.io, because its local, but still...)
  

## How can I make changes to the Keycloak realm persistent
The `microservices` realm is recreated from `microservices-realm.json` whenever the keycloak container is freshly started.
In order to change the realm, you have to make your changes through the admin console and when you're happy export the realm:
- Widen permissions on the `keycloak-volume` directory so the container default user can write into it: `chmod 777 keycloak-directory/`
- Run the export (while the service is running):\
  `docker compose exec -it keycloak /opt/keycloak/bin/kc.sh export --realm microservices --dir /mnt/volume --users realm_file`\
  (The logging output will end in an error because Keycloak is running, but that is not relevant. Keep reading further up for the export result)