# [insertProductNameHere]

## CSDS 395 - Senior Project Spring 2025

### Team Members

- Adriana
- Avalon
- Jaiden
- Jenny
- Levi
- Sarah
- Stephen

---

### Versioning and Dependencies

- Java 21.0.6

### Repository Guidelines

Naming Conventions:

- Classes and Interfaces: PascalCase
    - Use capital letters at the start of each word, **including** the first word of the Class or Interface.

    ```java
    public Interface Cat {
        // Add cat-related methods here
    }

    public Class MaineCoone implements Cat {
        // Act like a cat
    }
    ```

- Methods and Variables: camelCase
    - Use capital letters at the start of each word, **exlcuding** the first word of the Method or Variable.

    ```java
    private String catName;
    
    private shedEverywhere(boolean shouldAlsoMakeHairball) {
        // make a mess
    }
    ```

- Constants: SCREAMING_SNAKE_CASE
    - Use all caps, with words seperated by underscores.

    ```java
    public static final int MAXIMUM_CATS_IN_HOUSEHOLD = 7
    ```

Repo Conventions:

- Utilize unique branches for each unique feature; do not re-use a branch that was previously merged
- All branches should be derived from `dev`, and all branches should merge back into `dev`.
    - `dev` will be merged into `main` by Levi or Avalon on a weekly basis, by 11:30 AM on the Tuesday of each sprint.
        - At this point, `dev` and `main` will be identical.

Coding Conventions:

- Try to use constants that are well defined where possible
    - This makes it *much* easier to figure out what code does, and it helps whoever is looking at your code debug and understand why you made the choices you did
