package org.zeroref.borg.sagas;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.sagas.domain.*;

public class SagasTest {

    public static final String EMAIL = "user@gmail.com";

    @Test
    public void mapping_red_key() {
        SagasMapping mapping = new SagasMapping();
        mapping.map(TrialActivated.class, TrialActivated::getUserEmail);

        Assert.assertEquals(mapping.readKey(new TrialActivated(EMAIL)), EMAIL);
    }

    @Test
    public void storage_state_not_found_null() {
        SagaStorage storage = new SagaStorage();

        Assert.assertNull(storage.getById("22"));
    }

    @Test
    public void storage_get_by_id() {
        SagaStorage storage = new SagaStorage();
        storage.sagas.put(EMAIL, new TrialPolicyState());

        Assert.assertNotNull(storage.getById(EMAIL));
    }

    @Test
    public void storage_save() {
        SagaStorage storage = new SagaStorage();

        TrialPolicyState state = new TrialPolicyState();
        state.setSagaId(EMAIL);

        storage.store(state);

        Assert.assertTrue(storage.sagas.containsKey(EMAIL));
    }

    @Test
    public void dispatch_with_map_will_apply_to_existing() {
        SagaStorage storage = new SagaStorage();
        TrialPolicyState policyState = new TrialPolicyState();
        policyState.setSagaId(EMAIL);
        policyState.setType(TrialPolicy.class.getTypeName());
        storage.sagas.put(EMAIL, policyState);

        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(TrialPolicy.class);

        TrialCancelled o = new TrialCancelled();
        o.setUserEmail(EMAIL);
        persistence.dispatch(o);

        Assert.assertTrue(policyState.isCancelled());
    }

    @Test
    public void dispatch_with_map_will_no_op() {
        SagaStorage storage = new SagaStorage();
        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(TrialPolicy.class);

        TrialCancelled o = new TrialCancelled();
        o.setUserEmail(EMAIL);
        persistence.dispatch(o);
    }

    @Test
    public void dispatch_with_create_mapping_will_create_saga_if_missing() {
        SagaStorage storage = new SagaStorage();
        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(TrialPolicy.class);

        persistence.dispatch(new TrialActivated(EMAIL));

        Assert.assertNotNull(storage.sagas.get(EMAIL));
    }

    @Test
    public void dispatch_for_mark_completed_will_purge_storage() {
        SagaStorage storage = new SagaStorage();
        TrialPolicyState policyState = new TrialPolicyState();
        policyState.setSagaId(EMAIL);
        policyState.setType(TrialPolicy.class.getTypeName());
        storage.sagas.put(EMAIL, policyState);

        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(TrialPolicy.class);

        TrialExpired o = new TrialExpired();
        o.setUserEmail(EMAIL);
        persistence.dispatch(o);

        Assert.assertEquals(storage.sagas.size(), 0);
    }

    @Test
    public void lists_mapped_events_as_subscriptions() {
        SagaPersistence persistence = new SagaPersistence(new SagaStorage());
        persistence.register(TrialPolicy.class);

        Assert.assertTrue(persistence.isSagaSubscription(new TrialExpired()));
        Assert.assertFalse(persistence.isSagaSubscription(new Object()));
    }
}
