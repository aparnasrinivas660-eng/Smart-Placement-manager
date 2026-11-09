# PlacePrep – Smart College Placement Preparation and Recruitment Manager

A JavaFX desktop application for student profiles, MCQ practice tests, company eligibility checks and placement reports.

## Requirements

- JDK 17 or later (JDK 21 recommended)
- Visual Studio Code
- VS Code Extension Pack for Java
- Apache Maven 3.8+

## Run in VS Code

1. Extract the project ZIP.
2. Open the `PlacePrep` folder in VS Code (the folder containing `pom.xml`).
3. Open Terminal → New Terminal.
4. Check Java: `java -version`
5. Check Maven: `mvn -version`
6. Run:

   ```bash
   mvn clean javafx:run
   ```

Maven downloads JavaFX dependencies on the first run. Internet access is required for that first download.

## Storage

The application saves its data in `data/placeprep.dat` relative to the project folder. It creates this folder and file automatically. Reports are exported into the `reports` folder.

Do not delete `data/placeprep.dat` if you want to keep your saved records. Back it up before making major changes.

## Features in this MVP

- Register and view student profiles
- Store CGPA, branch, backlogs, skills and programming languages
- Add and delete aptitude/technical MCQs
- Conduct timed tests with automatic scoring
- Add and delete companies and eligibility criteria
- Find eligible students for each company
- Generate individual student performance reports
- Export student reports and company eligibility CSV reports

## Team division

- Member 1: `ui/StudentModule.java` and student model review
- Member 2: `ui/TestModule.java`, `model/Question.java`, `model/TestResult.java`
- Member 3: `ui/RecruitmentModule.java`, `ui/ReportModule.java`, `service/EligibilityService.java`
- Integrator: `MainApp.java`, `DataStore.java`, shared model classes and final testing

## Important integration rules

- Keep the package names and public method signatures unchanged.
- All modules use the same `AppData` object.
- Each module calls the supplied `onChange` callback after adding or deleting data.
- Commit changes in separate branches or files, then integrate and test together.

## Notes

This is a hackathon-ready educational MVP, not a production authentication system. It does not implement real user passwords, network security or a database server. Add those only if time and judging requirements permit.
