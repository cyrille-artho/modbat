package modbat.genran;

import randoop.CheckRep;

import static modbat.genran.State.*;

public class Host {

    private State state;

    public Host()
    {
        state = IDLE;
    }

    public void syn(State neighbor)
    {
        if(state == IDLE && neighbor == IDLE)
            state = SYN;
    }

    public void syn_ack(State neighbor)
    {
        if(state == IDLE && neighbor == SYN)
            state = SYN_ACK;
    }

    public void ack(State neighbor)
    {
        if(state == SYN && neighbor == SYN_ACK)
            state = ACTIVE;
    }

    public void active(State neighbor)
    {
        if(state == SYN_ACK && neighbor == ACTIVE)
            state = ACTIVE;
    }

    public void connectionError(State neighbor)
    {
        if(state == ACTIVE && neighbor == ACTIVE)
            state = CONNECTION_ERROR;
    }

    public void idle()
    {
        state = IDLE;
    }

    @CheckRep
    public boolean isConnectionError()
    {
        return state == CONNECTION_ERROR;
    }

    public State getState()
    {
        return state;
    }
}
