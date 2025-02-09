package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.internal.artifacts.dependencies.ProjectDependencyInternal;
import org.gradle.api.internal.artifacts.DefaultProjectDependencyFactory;
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder;
import org.gradle.api.internal.catalog.DelegatingProjectDependency;
import org.gradle.api.internal.catalog.TypeSafeProjectDependencyFactory;
import javax.inject.Inject;

@NonNullApi
public class TrailTalesFrontEndONEProjectDependency extends DelegatingProjectDependency {

    @Inject
    public TrailTalesFrontEndONEProjectDependency(TypeSafeProjectDependencyFactory factory, ProjectDependencyInternal delegate) {
        super(factory, delegate);
    }

    /**
     * Creates a project dependency on the project at path ":Trail-Tales"
     */
    public TrailTalesProjectDependency getTrailTales() { return new TrailTalesProjectDependency(getFactory(), create(":Trail-Tales")); }

    /**
     * Creates a project dependency on the project at path ":Trail-Tales-shared"
     */
    public TrailTalesSharedProjectDependency getTrailTalesShared() { return new TrailTalesSharedProjectDependency(getFactory(), create(":Trail-Tales-shared")); }

}
