# RERL
RERL is a novel framework for runtime enforcement of safe executions in component-based systems with multi-party interactions modeled using BIP. Our technique frames runtime enforcement as a sequential decision making problem and presents two alternatives for learning optimal strategies that ensure fairness between correct traces. We target both finite and infinite state-spaces. In the finite case, we guarantee that the system avoids bad-states by casting the learning process as a one of determining a fixed point solution that converges to the optimal strategy. Though successful, this technique fails to generalize to the infinite case due to need for building a dictionary, which quantifies the performance of each state-interaction pair. As such, we further contribute by generalizing our framework to support the infinite setting. Here, we adapt ideas from function approximators and machine learning to encode each state-interaction pairs' performance. In essence, we autonomously learn to abstract similar performing states in a relevant continuous space through the usage of deep learning. 

RERL is equipped with a command line interface that accepts a set of configuration options. 
It takes the name of the input BIP file, a file containing the bad states (explicitly or symbolically) to be avoided,  and optional flags (e.g., discount factor, number of episodes, horizon length), and it automatically generates a C++ implementation of the input BIP system embedded with a policy to avoid bad states. 

```
java -jar RERL.jar - A tool for autonomous learning correctness at runtime!

Usage: java -jar RERL.jar [options] input.bip output.cpp badStates.txt

where:

input.bip     = input BIP file name (required)
output.cpp    = output file to be automatically generated (required)
badStates.txt = File containing bad states to be avoided (required)

and options are:

-?                 prints usage to stdout; exits (optional)
-bad <s>           Bad reward value (default -1.0) (optional)
-capacity <n>      Capacity of memory replay (default 100) (optional)
-discount <s>      Discounting factor (default 0.9) (optional)
-episodes <n>      Number of episodes (default 10) (optional)
-epoch <n>         Epoch (default 10) (optional)
-exploration-factor <s> 
                   Probability of exploration (default 0.2) (optional)
-fair <s>          Fairness degree (default no fairness, i.e., <= 0) (optional)
-good <s>          Good reward value (default 1.0) (optional)
-h                 prints usage to stdout; exits (optional)
-help              displays verbose help information (optional)
-hidden <n>        Number of neurons in hidden layer (default 100) (optional)
-horizon <n>       Minimum trace length (trace length is guaranteed to be
                   greater than "minimum trace length" and "diameter of all
                   atomic components" - default minimum trace length is 15)
                   (optional)
-max-iteration <n> Bound iteration (default 100000) (optional)
-mode <s>          
                   *finite: for finite case 
                   	options used: discount, good, max-iteration
                   *infinite: for infinite case.
                   	options used: all except max-iteration
                   *standard: without reinforcement learning
                   	(no options - default)
                    (optional)
-percentage-sample <n> 
                   Percentage of samples from replay memory (default 50%)
                   (optional)
-period-reset <n>  Reset period time, i.e., theta- (default 2) (optional)
-version           displays command's version (optional)

Option tags are not case sensitive, and may be truncated as long as they remain
unambiguous.  Option tags must be separated from their corresponding values by
whitespace, or by an equal sign.  Boolean options (options that require no
associated value) may be specified alone (=true), or as 'tag=value' where value
is 'true' or 'false'.
```
