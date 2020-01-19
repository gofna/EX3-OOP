**Welcome to The Hungry Robot!**

the hungry robot is a game, writing in Java, that allow you to play with robots, and try to eat fruits as much as you can, before the time will end!

**Description:**

This program has 24 scenarios to play. Each scenario is deferent and contain graph, number of fruits on the graph, number of robots and the time to play.

In addition, the game have 2 options – to play the game manually with the mouse , or watch an automatic effective game.

For the beginning – you have to choose the mode game by clicking "start" in the top left corner –  and choose automatic or  manual game.

See the picture bellow:

![picture to git (2)](https://user-images.githubusercontent.com/57867811/72685186-3ee55380-3af0-11ea-9608-b98dd4066235.png)


Now, you need to choose a scenario number (0-23).

In the automatic mode, the game will start.

In the manually mode, you have to choose a nodes to place the robot wherever you want in the graph and start to play with the mouse by clicking on the robot you want to move and then the  vertices you want to move the robot. Note that you can move only 1 or 3 vertices each time.

See example game (scenario number 11)- 
![game (2)](https://user-images.githubusercontent.com/57867811/72685326-d13a2700-3af1-11ea-9f80-c345ce27c8f7.png)

 
**Structure:**

The program contains several packages and classes. Here the main of them-

Algorithms and DataStructure packages:

This project based on our previous project- Graphs. And those packages from Graphs project.

here's a link to the Graphs project for more information-

https://github.com/maor6/Graphs

The use of this packages is that the graph is the infrastructure to the game. All the game happening on this graph. 
In addition, the main use of this algorithms is for the move of the robots – in shortest path.

GameClient-

Autogame class represent an automatic game.
 the class allowing an effective automatic game,
 by move the robots to an edge on the graph with a fruit, in shortest path.
 
MyGameGUI class represent a graphical game by using the GameServer API.
the class
 allowing to choose a scenario to the game, place the robots and play the game
 manually or watch an automatic mode game. 
 in addition, the class allowing to
 displays the score, and the time left to the end of the game.
 the manually
 game allow to play the game by mouse press (on a robot, and the on node on
 the graph).
 
KML_loger class create a KML file to the automatic game from the class "MyGameGui". 
the name of the file will be the scenario number of the game, and the file will save in a folder call "KML_games".
SimpleGameClient class represents a simple example for using the GameServer API.

gui-

Graph_GUI cless display a graph on a window. to show the graph optimally, the
class make all the adjustments such as: - change the scales window according
to the vertices value and edges on the graph. - put an image in size that


for more details and information about the program you can find on wiki.
Enjoy the game!

