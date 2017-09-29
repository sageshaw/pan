package analysis.ops;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface OpScript {


    boolean displayHistogram() default false;

    boolean exportable() default false;

    Class <? extends AnalysisOperation> type();

    String label();
}
