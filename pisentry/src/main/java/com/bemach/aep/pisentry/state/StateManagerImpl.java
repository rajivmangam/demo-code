package com.bemach.aep.pisentry.state;

import org.apache.log4j.Logger;

import com.bemach.aep.pisentry.vos.Event;
import com.bemach.aep.pisentry.vos.EventType;
import com.bemach.aep.pisentry.vos.State;

public class StateManagerImpl implements StateManager {
	private static Logger logger = Logger.getLogger(StateManagerImpl.class);

	private static State state = State.UNARMED;
	private static StateManagerImpl instance;
	private NotificationManager notificationMgr;

	private StateManagerImpl() {
	}

	public void setNotificationManager(NotificationManager notificationManager) {
		this.notificationMgr = notificationManager;
	}

	public void process(Event event) {
		State lastState = state;

		switch (event.getType()) {
		case FAULT:
			if (state == State.ARMED_AWAY) {
				state = State.ALARMED;
			} else if (state == State.ARMED_HOME) {
				// for motion don't alarm
				state = State.ALARMED;
			}
			break;
		case ARM_AWAY:
			state = State.ARMED_AWAY;
			break;
		case ARM_HOME:
			state = State.ARMED_HOME;
			break;
		case DISARM:
			state = State.UNARMED;
			break;
		default:
			break;
		}

		if (lastState != state) {
			notify(lastState, state);
		}
	}

	private void notify(State currentState, State newState) {
		String data = String.format("%s to %s", currentState, newState);
		Event event = new Event("StateManagerImpl", EventType.NOTIFY, data);
		notificationMgr.notify(event);
		logger.info(String.format("Notifying %s", event.toString()));
	}

	public State getState() {
		return state;
	}

	/**
	 * Spring Framework would help here ...
	 * 
	 * @return
	 */
	public static StateManagerImpl getInstance() {
		if (instance == null) {
			instance = new StateManagerImpl();
		}
		return instance;
	}

}
