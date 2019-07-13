package modbat.genran;

public class TCPProtocol {

    private Host alice;
    private Host bob;
    private int timer;

    public TCPProtocol() {
        alice = new Host();
        bob = new Host();
        timer = 0;
    }

    public void synAlice() {
        alice.syn(bob.getState());
        valid();
    }

    public void ackAlice() {
        alice.ack(bob.getState());
        valid();
    }

    public void synackAlice() {
        alice.syn_ack(bob.getState());
        valid();
    }

    public void synBob() {
        bob.syn(alice.getState());
        valid();
    }

    public void ackBob() {
        bob.ack(alice.getState());
        valid();
    }

    public void synackBob() {
        bob.syn_ack(alice.getState());
        valid();
    }

    public void connectionError() {
        if (alice.getState() == State.ACTIVE && bob.getState() == State.ACTIVE) {
            throw new RuntimeException("connectionError");
        }
    }

    private void valid() {
        timer++;

        if (timer % 10 == 0) {
            alice.idle();
            bob.idle();
        }
    }
}
