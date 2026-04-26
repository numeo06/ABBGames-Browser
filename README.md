# ABBGames-Browser

Java Swing application with searchable board game database featuring multi-filter search, user collections, and reviews.

## Technologies

- Java Swing
- XML persistence (DOM-based)
- Design Patterns: MVC, Template Method, Composite, Observer

## Features

- Search and filter 90+ board games by name, category, and mechanic
- Create and manage custom game collections
- User review and rating system (1-5 scale)
- O(1) lookup performance with HashMap indexing

## Getting Started

1. Clone the repository
2. Open in IntelliJ IDEA
3. Run `MainFrame.java` to start the application
4. Default login: username/password (or create a new account)

## Future Enhancements

- PostgreSQL backend refactor
- BoardGameGeek API integration for real-time game data
- Enhanced filtering and sorting options

## Architecture

The application follows a 3-tier MVC architecture with design patterns for scalability:
- **Data Layer**: HashMap-indexed storage with DOM-based XML persistence
- **Model Layer**: Game, User, UserCollection, Admin, Review classes
- **View/Control Layer**: Java Swing UI with panel-based navigation (Facade pattern)
