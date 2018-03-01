# CHANGELOG

## Version 1.0.1 (work in progress)
- Added more functional types:
 - Additional Tuple types (`T3`, `T4`)
 - Additional Function Types (`Function0` - `Function4`)
 - Added Function chaining to all function types
 - Added Function currying and partial application
 - Integrated Failable Functions with *normal* Functions for better interoperation (`FailableFunction0` & `FailableFunction1`)
 - Removed `FailableRunnable`
- Added an immutable `List` implementation similar to the Scala `List` built as a single linked stack with immutable elements
- Added `Callable` as a super-type of `FailableFunctio0` to allow for seamless integration with `ExecutorServices`	
- Made all Tuple types `Serializable`
- Added `serial` package containing interface for different serialization schemes. These interfaces explicitly declare the methods required for customized Java serialization:
 - `CustomDeserialization` for objects requiring a `readObject` method
 - `CustomSerialization` for objects requiring a `writeObject` and a `readObject` method
 - `SerializedThroughProxy` for objects that are serialized as another (proxy) object provided by a `writeReplace` method
 - `SerializationProxy` for objects that serve as proxy objects in serialization and are resolved through a `readResolve` method. If a class serves as its own proxy (i.e. after normal serialization the object will be replaced when deserializing it), the class can implement `SerializationProxy` itself. There is no need to use a distinct proxy object.

## Version 1.0.0
- Migrated various utilities from other projects. These include:
 - Several functional types such as <code>Try</code>, <code>Result</code>,
							<code>FailableFunction0</code>, <code>FailableFunction1</code>
							and <code>T2</code> (a 2-tuple)
 - XML processing similar to X-Path
 - <code>IterativeRunner</code>: A runnable that evaluates the same code block in an infinite loop that can be terminated by
							the code block itself or by interrupting the thread running the <code>Runnable</code>.
 - a <code>Source</code> system simplifying and unifying
							reading data from files, streams or a URL
 - a <code>PriorityQueue</code> using numeric priority parameters instead of natural ordering of the elements.
 - a wrapper class for <code>Lazy</code> instantiation
 - a <code>Shutdown</code> handler interacting with Java
							shutdown hooks that allows the definition of a specific shutdown
							sequence for shutdown hooks.

## Version 0.0.6
- added <code>Logic</code> system as an extension to the Java
					<code>BooleanSupplier</code> interface. <code>Logic</code> allows
					boolean operations to be done on these provider functions to enable
					complex behaviour. In essence any boolean composition that could be
					done by first evaluating multiple <code>BooleanSupplier</code>
					instances and then applying the boolean operations can be done on
					the <code>Logic</code> instances such that the complete expression
					can be evaluated by evaluating a single <code>Logic</code>
					instance. Some optimisation similar to classical <em>short
						circuiting</em> is done (i.e. once the result of an expression is fully
					known any additional providers are not evaluated).
- added <code>Sequence</code> for composing runnables into a
					predefined interruptible sequence that can be executed
					asynchronously.

## Version 0.0.5
- added helper methods to <code>MachineCoreBuilder</code> for creation of event sequences, event funnels and arbitrary sequence event chains (event sequences for any permutation of a set of events)
- <code>MachineCoreBuilder</code> can provide target states for a given origin state and event
- added <code>CommandProcessor</code> as a pre-built asynchronous processor that consumes commands from a <code>StateMachine</code> and passes them on to a given <code>Consumer</code> function
- added <code>EventGenerator</code> as an asynchronous way to produce and issue events in a fixed rhythm. The events may either be fixed or supplied by a <code>Supplier</code> function
- <code>StateMachine</code> states can be stored to and loaded from <code>StateMachine.SavedState</code> objects
- logging support using <code>java.util.logging.Logger</code>
- the structure of a <code>StateMachine</code> can be extracted into raw <a href="http://www.graphviz.org">Graphviz</a> data
- machine cores now support terminal states
- terminal state checking controllable through <code>MachineCoreBuilder</code>
- introduced new event handling modes for <code>StateMachine</code>:
 - <em>priority mode</em> (default) processes internal events before external
 - <em>normal mode</em> enqueues internal processes in the main event queue
- improved documentation

## Version 0.0.4
- created <code>ch.awae.utils.statemachine</code> package for creating asynchronous state machine clusters:
 - use <code>MachineCoreBuilder</code> to create state machine cores
 - use <code>StateMachineBuilder</code> to create state machine clusters

## Version 0.0.2
- added MIT license file
- documented <code>Trampoline</code> class
 
## Version 0.0.1
- initial release