package com.bemach.aep.pisentry.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.bemach.aep.pisentry.vos.Event;
import com.bemach.aep.pisentry.vos.EventType;
import com.bemach.aep.pisentry.vos.Notification;
import com.bemach.aep.pisentry.vos.State;

public class StateManagerTest {
	
	private StateManager target;
	
	@Mock
	private Event arm;

	private NotificationManager notifier;

	private Notification msg;

	@Before
	public void setUp() {
		notifier = mock(NotificationManager.class);
		msg = mock(Notification.class);
//		Mockito.doNothing().when(notifier).notify(Mockito.any(Notification.class)); by default mock is to do nothing.
		target = new StateManagerImpl(notifier);
	}
	
	@Test
	public void should_stay_unarmed_when_unarmed_and_get_fault() {
		Event fault = mock(Event.class);
		when(fault.getType()).thenReturn(EventType.FAULT);
		
		target.process(fault);
		
		State state = target.getState(); 
		assertNotEquals(State.ALARMED, state);
	}
	
	@Test
	public void should_become_alarmed_when_armed_away_and_get_fault_event() {
		Event fault = mock(Event.class);
		Event arm = mock(Event.class);
		when(arm.getType()).thenReturn(EventType.ARM_AWAY);
		when(fault.getType()).thenReturn(EventType.FAULT);
		target.process(arm);
		
		target.process(fault);
		
		State state = target.getState(); 
		assertEquals(State.ALARMED, state);
	}
	
	@Test 
	public void should_become_unarmed_when_armed_away_and_get_disarm_event() {
		Event disarm = mock(Event.class);
		Event arm = mock(Event.class);
		when(arm.getType()).thenReturn(EventType.ARM_AWAY);
		when(disarm.getType()).thenReturn(EventType.DISARM);
		target.process(arm);
		
		target.process(disarm);
		
		State state = target.getState(); 
		assertEquals(State.UNARMED, state);
	}
	
	@Test
	public void should_notify_when_state_changes() {
		Event disarm = mock(Event.class);
		Event arm = mock(Event.class);
		when(arm.getType()).thenReturn(EventType.ARM_AWAY);
		when(disarm.getType()).thenReturn(EventType.DISARM);

		target.process(arm);
		
		verify(notifier, times(1)).notify(any(Notification.class));
	}
}
