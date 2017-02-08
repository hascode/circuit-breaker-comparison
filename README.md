# Resilient Architecture: Java Circuit Breakers

Examples for integrating different Java circuit-breaker implementations into an application.

## Circuit-breaker libraries used:
* [Hystrix]
* [Failsafe]
* [Javaslang Circuit-Breaker]
* [Vert.x Circuit Breaker] 

## Running the Examples

### Failsafe

```
$ mvn exec:java -Dexec.mainClass=com.hascode.tutorial.FailsafeExample
```

### Javaslang

```
$ mvn exec:java -Dexec.mainClass=com.hascode.tutorial.JavaslangExample
```

### Hystrix

```
$ mvn exec:java -Dexec.mainClass=com.hascode.tutorial.HystrixExample
```

### Vert.x

```
$ mvn exec:java -Dexec.mainClass=com.hascode.tutorial.VertxExample
```

## More
For more of my experiments please feel free to visit my blog at [www.hascode.com] or to have a look at my [project repositories].

----

**2017 Micha Kops / hasCode.com**

  [www.hascode.com]:http://www.hascode.com/
  [project repositories]:https://bitbucket.org/hascode/
  [Hystrix]:https://github.com/Netflix/Hystrix
  [Failsafe]:https://github.com/jhalterman/failsafe
  [Javaslang Circuit-Breaker]:https://github.com/RobWin/javaslang-circuitbreaker
  [Vert.x Circuit Breaker]:http://vertx.io/docs/vertx-circuit-breaker/java/
