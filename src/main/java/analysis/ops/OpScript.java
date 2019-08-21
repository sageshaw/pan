package analysis.ops;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to denote new algorithm plugins.
 */
@Deprecated
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface OpScript {


    Class <? extends AnalysisOperation> type();

    String label();
}
