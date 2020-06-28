# code_complexity_embeddings

This repository contains the code for obtaining basic code complexity metrics from Java methods.

---

## Set of Complexity Metrics:

- LOC - Lines of code.
- Block - Number of block statements.
- BasicBlock - Number of basic blocks.
- Parameter - Number of parameters.
- LocalVariable - Number of variable declarators.
- GlobalVariable - Number of simple name in expressions.
- Loop - While + Do + For + ForEach.
- Jump - Break + Continue + Return.
- Decision - If + IfElse + Else.
- Condition - UnaryExpr + BinaryExpr + ConditionalExpr.
- Instance - Number of object creation.
- FunctionCall - Number of method call.
- ErrorHandler - Try + Catch + Finally + Throw.
- ThreadHandler - Runnable + Thread + Callable + Synchronized.
- This - Number of this node.
- Super - Number of super node.
- Null - Number of null node.
- Boolean - Number of boolean node.
- Ternary - Number of ternary node.
- Return - Number of return node.
- ASTNode - Number of AST nodes.
- ASTToken - Number of all tokens.

---

## Execute JavaComplexityMetrics:

  * args[0] = Input directory to Java methods.
  * args[1] = Output directory to save a CSV file.
  
  ```
  $ cd <JavaComplexityMetrics>
  $ mvn clean compile assembly:single
  $ java -jar target/jar/JavaComplexityMetrics.jar <input_directory> <output_directory>
  ```
  
  - A **"java_complexity_metrics.csv"** file will be created in the output directory. Each row of the CSV file contains the complexity metrics for each method as **"path,method,LOC,...,ASTToken"**.
  
---

### Related Work: https://github.com/mdrafiqulrabin/handcrafted-embeddings