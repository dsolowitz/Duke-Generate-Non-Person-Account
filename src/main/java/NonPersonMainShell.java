import asg.cliche.ShellFactory;

import java.io.IOException;

public class NonPersonMainShell {

    public static void main(String[] args) throws IOException {
        ShellFactory.createConsoleShell("Lets generate some non-person accounts", "", new NonPersonAccount())
                .commandLoop(); // and three.
    }
}
