mpoems-in-java
--------------

mPOEMS in JAVA is designed to provide optimisation problem developers with a tool to apply mPOEMS to their problem, without the need to know the complex algorithm in detail. Main goal is to use a state-of-the-art optimisation algorithm with small development effort.


The JAVA implementation of mPOEMS
---------------------------------

mPOEMS in JAVA is the generic JAVA implementation of an evolutionary algorithm called Multiobjective Prototype Optimization with Evolved Improvement Steps (mPOEMS). The framework was designed to provide optimisation problem engineers with an interface to use mPOEMS, without detailed understanding of the algorithm.

All needed methods and fields to use the framework are presented in the documentation of the framework which can be found at the following location:

     http://thomaskremmel.com/mpoems/mpoems_in_java_documentation.pdf .



About mPOEMS
------------

Crossover, mutation, and evolution of a population are the basic principles of the evolution and natural selection. Evolutionary algorithms (EA) imitate the nature’s behaviour, and adapt the basic principles, to evolve a set of solutions, towards the optimum solution of a problem.

mPOEMS is an optimisation framework, in the domain of evolutionary algorithms. It was designed to solve optimisation problems, with an unrestricted number of objectives. The algorithm has shown excellent performance in comparison with other state-of-the-art search algorithms. The paper including these results can be found using google scholar or directly using this link.

The drawback of Evolutionary Algorithms is their complexity, and the development effort one has to conduct to create an implementation of such an algorithm. An impressing approach to test and compare various evolutionary algorithms, with little programming effort, is presented by the PISA project. The project website can be found by following this link.

PISA is a platform and programming language independent interface for search algorithms. The use of PISA ensure easy, fast and reliable comparison of different optimization algorithms on various problems or benchmarks. With litte programming effort, one can create the problem specific part of an optimisation problem, and combine it with arbitrary ready-to-use, pre-compiled and very complex optimisation search algorithms.

The project "mPOEMS in JAVA" shares the goal of providing an easy-to-use framework, to use complex search algorithms, with PISA. The PISA framework is applicable to almost any search algorithm. mPOEMS in JAVA focus on providing a framework to use the evolutionary algorithm mPOEMS, without the need to know the algorithm in detail.


The project was initially published at google code: http://code.google.com/p/mpoems-in-java/


Authors
--------

The framework was created at the Technical University of Vienna and was a major part of the authors (Thomas Kremmel) thesis. 

Dr. Jiří Kubalík and Prof. Dr. Stefan Biffl had the idea for this project and supported / supervised the progress.
Dr. Jiří Kubalík has written the optimization algorithm the framework implements. He is an assistant professor at the Department of Cybernetics at the Czech Technical University in Prague:

    http://cyber.felk.cvut.cz/people/page.php?id=38&detailed=y

Prof. Dr. Stefan Biffl is associate professor of software engineering at the Institute of Software Technology and Interactive Systems, Vienna University of Technology.

    http://qse.ifs.tuwien.ac.at/~biffl/


Disclaimer
------------

This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with mpoems-in-jave.  If not, see <http://gnu.org/licenses/>.


Getting Started
------------

Best starting point is to read the documentation .

To see the framework in action just download the ready-to-start .jars at the downloads tab. There you can find two downloads. A mpoems-in-java solution for the knapsack problem and the .jar for the project selection problem. Get more information about the knapsack problem at wikipedia.

With JAVA installed you can start the .jars with double-clicking on them and waiting for a while, checking out the resulting .xls after a few minutes or you can use this command to see the command-line output:

    * java -jar tuwien.ifs.mpoems.knapsackProblem-1.0-SNAPSHOT-all.jar
    * java -jar tuwien.ifs.mpoems.ppds.start-1.0-SNAPSHOT-all.jar
    * Please note that you have to create an output folder for the ppds jar in order to create the output file.

You can also change the settings mpoems-in-java is running with opening the .jar with win rar., change the settings file and then save it.

You can find the settings file in the folder

    * tuwien.ifs.mpoems.knapsackProblem-1.0-SNAPSHOT-all.jar\tuwien\ifs\mpoems\knapsackProblem\mPOEMSConf.cfg
or

    * tuwien.ifs.mpoems.ppds.start-1.0-SNAPSHOT-all.jar\tuwien\ifs\mpoems\ppds\start\mPOEMSConf.cfg
    * tuwien.ifs.mpoems.ppds.start-1.0-SNAPSHOT-all.jar\tuwien\ifs\mpoems\ppds\start\projectSelection.cfg

The docs folder section also provides a file with the most common maven commands. These commands should be enough to create an eclipse project out of the source code and get started.

