/**
 * The {@link ch.awae.utils.logic.Logic Logic} system provides an intuitive
 * extension to the {@link java.util.function.BooleanSupplier} that supports
 * boolean operators for more expressive solutions.
 * 
 * {@link ch.awae.utils.logic.Logic Logic} instances can be created directly
 * through lambda expressions or from a
 * {@link java.util.function.BooleanSupplier BooleanSupplier} through the use of
 * {@link ch.awae.utils.logic.Logic#from(java.util.function.BooleanSupplier)
 * Logic.from(java.util.function.BooleanSupplier)}.
 * 
 * In addition to these extensions the logic system also provides support for
 * {@Link ch.awae.utils.logic.LogicGroup LogicGroups}. These groups provide
 * support for counting-based evaluations (e.g. "true if exactly 3 members are
 * true") and group-based evaluations (e.g. "true if all members are true").
 * 
 * As an additional quality of life extension
 * {@Link ch.awae.utils.logic.LogicCluster LogicClusters} are provided. Clusters
 * are similar to groups in that they group multiple logic instances but they
 * evaluate directly to an integer representation of the evaluation results of
 * all members. This allows the simultaneous evaluation of up to 32 logic
 * instances and to then handle their results in a single {@code switch} block.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
package ch.awae.utils.logic;