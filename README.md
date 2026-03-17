# SC2002_Assignment
GitHub Repo for SC2002 Turn-based Assignment

---

## Project Structure
```
project/
├── src/        # Source code (.java)
├── out/        # Compiled files (.class)
├── .gitignore
└── README.md
```

---

## Requirements
- Java JDK 8 or higher  

---

## Compilation

From the project root directory:

```bash
mkdir -p out

javac -d out src/Main.java \
src/game/*.java \
src/ui/*.java \
src/combatant/*.java \
src/action/*.java \
src/item/*.java \
src/statuseffect/*.java
```

---

## Running the Program

After compilation:

```bash
cd out
java Main
```
