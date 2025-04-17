# SonarQube Analysis Instructions

## Running SonarQube Server

1. Start SonarQube using Docker Compose:
   ```bash
   docker-compose up -d
   ```

2. Wait for the SonarQube server to start (it might take a minute or two)
   - You can check if it's running by going to http://localhost:9000 in your browser

3. Log in with default credentials:
   - Username: admin
   - Password: admin
   - You'll be prompted to change the password on first login

## Running the SonarQube Analysis

1. First, run your tests with JaCoCo to generate code coverage data:
   ```bash
   mvn clean verify
   ```

2. Then run the SonarQube analysis:
   ```bash
   mvn sonar:sonar
   ```

3. View the results at http://localhost:9000/dashboard?id=car-rental-app

## Sample SonarQube Report

After running the analysis, you should see a comprehensive report in SonarQube with:

- Code quality metrics
- Test coverage metrics
- Code smells, bugs, and vulnerabilities
- Security hotspots
- Duplications

For this project, you'll want to pay particular attention to:
- Unit test coverage of your service and repository classes
- Code smells in any complex business logic
- Proper handling of exceptions

## Common Issues and Fixes

If you encounter "Project Not Found" error:
- Ensure the `sonar.projectKey` in your pom.xml matches the project key in SonarQube

If you see "Not Authorized" error:
- Generate a token in SonarQube (User > My Account > Security)
- Run the analysis with: `mvn sonar:sonar -Dsonar.login=YOUR_TOKEN`
