# engine_prototype

Prototype 2D Game Engine, coded in Java. This is a project that I began while in my second year of university, and unfortunately wasn't able to make much progress on beyond broad-phase Physics handling and rudimentary graphical rendering. It primarily uses graphics from a [project](https://github.com/bhunt02/angelboy/tree/master) I was working on prior to it, but also gave me an opportunity to design more (low-resolution) seamless tiles. 

## Current Features
- Physics Engine ruled by Dynamic Area Tree for more efficient object-to-object interaction processing. (Incomplete)
- 2D layered Graphics rendering. (Incomplete)
    - Designed to take advantage of Parallax Scrolling
- User input processing via Swing Input and Action Maps and AWT KeyEvents
- Concurrent Data Structures (Sets, Lists) for shared use between threads
- Various classes related to storing level data, sprite data, color data, (game) object data, etc.
