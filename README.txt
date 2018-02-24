COMP 452 - Artificial Intelligence for Game Developers

This course teaches knowledge and skills in AI techniques. AI techniques are used to generate efficient intelligent behaviour in games, with additional attention to algorithms that improve gameplay experience.
2 of my assignemnts are here with video output of the results.


TME 1 - Steering Behaviours Implementation

	This is basically a proof of concept for a simple game. The player controls a pizza and has to collect toppings randomly placed in the game area for points. The computer controls an opponent which is a pizza chef on the right-hand side of the gameplay area. The chef moves up and down throwing knives at the current player position. These knives travel in a straight line until they leave the gameplay area. Occasionally, the chef will also throw a pizza cutter. This weapon will track the player and consisstenetly head towards them for a few seconds, until eventually heading in a straight line to the edge of the gameplay area. Over time the speed of the chef and his weapons gradually increases. The chef and his weapons implement basic steering behaviours. The chef and knife use an arrive behaviour, where the travel in a consistent direction to their end point. Th pizza cutter uses a seek behaviour, seeking the player position until switching to an arrive behaviour after a few seconds have passed.

	The player character is a pizza that can be controlled with the mouse. There are a number of toppings following the pizza, that keep track of the number of each topping type collected. These toppings implement a more comoplex flocking behaviour, where they seek an area directly behind the player character but are designed to behave as a flock (ie a flock of birds). The player gets points by collecting toppings and the game ends when they have been hit by the weapons 4 or 5 times.


TME 2 - Pathfinding and Decision Making

	Game 1 - A* Pathfinding

		This game is a 16x16 grid where the player creates the start and end points. After these are created, the player can change the other tiles to different types of obstacles. The brown colour is open terrain, with a movement cost of 1. The light green colour is grassland, with a movement cost of 3. The dark green colour represents swampland, with a movement cost of 4. Finally, the red X is an obstacle that cannot be traversed. Once the player has created the terrain, the computer will use the A* pathfinding algorithm to calculate the best path from the start point to the goal. Once the path has been found the display area to the right will show the results.

	Game 2 - Finite State Machine

		This game simulates ant behaviour. The play grid is similar to that in Game 1. Each ant first moves randomly to find a piece of food on the game board. Once found, it will return to the home position. The ant is then thirsty, so it will move randomly in search of water. Once water is found, it returns to the food finding behaviour. Each time an ant returns home with food, a new ant is born and will follow the prescribed behaviour. The population grows as long as food continues to be brough to the home square. The environment is filled with different types of cells: open terrain, food, water, and poison. An ant can walk safely on all cells except poison, where it will die. The user can specify the starting number of ants in the population, and each ant uses the pathfinding algorithm from Game 1 to return home.


TME 3 - Minimaxing and Learning

	Game 1 - Minimaxing

		This is a simulation of a Connect 4 game with the player in white vs. the computer player in black. The player can introduce chips from the top row of the game grid, where the piece then falls into position. The first player to have 4 chips in a row is the winner. The game implements alpha-beta pruning with a depth limit of 4 for the computer in a mini-max search.

	Game 2 - Learning

		Unfortunately, the output for this game is not available. The game is the pizza game from TME 1. It implements a hill-climbing learning algorithm.


*** ALL CODE CREATED BY JASON BISHOP. IF YOU WISH TO COPY OR USE THESE FILES IN ANY WAY, PLEAS ASK PERMISSION ***