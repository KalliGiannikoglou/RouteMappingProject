# General Description
The present repository includes all the development process of the Algorithmic part of my thesis "Route mapping of a city using OpenStreetMap and planning for optimal traversal".
The general idea is that we want, in the final stage, to schedule delivery agents which are receiving packages from all around the city and are delivering them accordingly.
We implement algorithms for assigning the packages to the agents, for order of traversal for each agent and for optimal path-finding between points. 
All the code is implemented in Java.

## Phases of development:
The development was divided in 4 stages:
1. Route Mapping between two points, using A* Search
2. Route Mapping between one source point and multiple destinations
3. Route Mapping between multiple source and multiple destination points.
4. Assign packages to available delivery agents and repeat step 3.

### Licence
In our implementation K-D trees are used. 
The code for this implementation belongs to Justin Wetherwell, is publicly available in this [repository](https://github.com/phishman3579/java-algorithms-implementation/blob/master/src/com/jwetherell/algorithms/data_structures/KdTree.java) and it is under Apache [license](https://github.com/phishman3579/java-algorithms-implementation/blob/master/LICENSE) allowing reuse and modification with proper attribution to the author.
