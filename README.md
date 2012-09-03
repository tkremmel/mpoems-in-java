mpoems-in-java
==============

mPOEMS in JAVA is designed to provide optimisation problem developers with a tool to apply mPOEMS to their problem, without the need to know the complex algorithm in detail. Main goal is to use a state-of-the-art optimisation algorithm with small development effort.


The JAVA implementation of mPOEMS
=================================

mPOEMS in JAVA is the generic JAVA implementation of an evolutionary algorithm called Multiobjective Prototype Optimization with Evolved Improvement Steps (mPOEMS). The framework was designed to provide optimisation problem engineers with an interface to use mPOEMS, without detailed understanding of the algorithm.

All needed methods and fields to use the framework are presented in the documentation of the framework which can be found at the following location:

http://thomaskremmel.com/mpoems/mpoems_in_java_documentation.pdf .



About mPOEMS
==============

Crossover, mutation, and evolution of a population are the basic principles of the evolution and natural selection. Evolutionary algorithms (EA) imitate the nature’s behaviour, and adapt the basic principles, to evolve a set of solutions, towards the optimum solution of a problem.

mPOEMS is an optimisation framework, in the domain of evolutionary algorithms. It was designed to solve optimisation problems, with an unrestricted number of objectives. The algorithm has shown excellent performance in comparison with other state-of-the-art search algorithms. The paper including these results can be found using google scholar or directly using this link.

The drawback of Evolutionary Algorithms is their complexity, and the development effort one has to conduct to create an implementation of such an algorithm. An impressing approach to test and compare various evolutionary algorithms, with little programming effort, is presented by the PISA project. The project website can be found by following this link.

PISA is a platform and programming language independent interface for search algorithms. The use of PISA ensure easy, fast and reliable comparison of different optimization algorithms on various problems or benchmarks. With litte programming effort, one can create the problem specific part of an optimisation problem, and combine it with arbitrary ready-to-use, pre-compiled and very complex optimisation search algorithms.

The project "mPOEMS in JAVA" shares the goal of providing an easy-to-use framework, to use complex search algorithms, with PISA. The PISA framework is applicable to almost any search algorithm. mPOEMS in JAVA focus on providing a framework to use the evolutionary algorithm mPOEMS, without the need to know the algorithm in detail.