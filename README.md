# User-scheduling-in-5G (MCKP)
(Jan - Feb 2020)  
Working on an Multiple Choice Knapsack Problem (which is an integer linear problem) to design optimal packet schedulers for 5G   
We will first proceed to a data preprocessing
Then we will use different approaches to solve this ILP :
- A greedy algorithm
- A dynamic programming algorithm
- A branch an bound algorithm
- A stochastic algorithm

THE PROBLEM ################################################################  
In 5G, an antenna transmits data packets to smartphones (or users) through a
wireless medium, which is divided into a set of frequency channels.
The higher the power dedicated to a user, the higher the data rate it can expe-
rience. The exact dependence between power and data rate is however user and
channel specific. With the same transmit power, a user close to the antenna will
enjoy for example a higher data rate than a user far away. A wireless packet sched-
uler is thus responsible to allocated channels to users and to divide the total power
budget of the antenna among the available channels. The goal of this project is to
design optimal packet schedulers in this context.  

The detailed subject and its tasks as well as test files are available on this page:  
https://marceaucoupechoux.wp.imt.fr/enseignement/english-inf421-pi/  


THE CODE ####################################################################  
The code is written in Java. read the following to run it.  

Modeling classes-------------------------------------------------------------  
We recommand you to have a quick look on the classes Instance and Solution.  
#) An object of the class instance is created using the path to a test file,  
   it contains all the needed information about the problem  
#) Instead of modeling the solution as a 3D matrix, we created a class Solution  
   that seemed to be more efficient  
The classes Doubly and Couple are auxilary classes, and don't have any particular methods  
The classes Instance and Doubly have main methods to test construction of their objects  

Classes answering to the questions ------------------------------------------  

The class Preprocessing answers to each of the questions 1,2,3,4 and 5.  
run the class to do the tests and print their results  

The class LP answers to the questions 6, 7  
run the class to do the tests and print their results  

The class DP answers to the questions 8 and 9 and partially 11  
run the class to do the tests and print their results  

The class BB answers to the questions 10 and partially 11  
run the class to do the tests and print their results  

The class OP answers to the questions 12 and 13  
run the class to do the tests and print their results  
