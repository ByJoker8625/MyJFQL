package de.byjoker.myjfql.network.cluster;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StandaloneCluster implements Cluster {
    @Override
    public void join() {
    }

    @Override
    public void quit() {
    }

    @Override
    public void registerWorker(@NotNull ClusterWorker worker) {
    }

    @Override
    public void unregisterWorker(@NotNull String id) {
    }

    @NotNull
    @Override
    public List<ClusterWorker> getWorkers() {
        return null;
    }

    @Nullable
    @Override
    public Object query(@NotNull String query, @NotNull String worker, @NotNull Continuation<? super Unit> $completion) {
        return null;
    }

    @Nullable
    @Override
    public Object balance(@NotNull String query, @NotNull Continuation<? super Unit> $completion) {
        return null;
    }

    @Nullable
    @Override
    public Object sync(@NotNull String query, @NotNull Continuation<? super Unit> $completion) {
        return null;
    }

    @Override
    public void shutdown() {
    }

}
