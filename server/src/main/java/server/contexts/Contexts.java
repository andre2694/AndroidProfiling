package server.contexts;

public class Contexts {

    public static int URL = 0;
    public static int HANDLER = 1;
    public static Object[][] contexts = {
            {"/code", new ConfirmCodeHandler()},
            {"/bootstrap", new BootstrapHandler()},
            {"/profile", new ProfileHandler()},
            {"/newnumber", new NewNumberHandler()},
            {"/update", new FullUpdateHandler()},
    };

    public static Object[][] auth = {
            {"/login", new LoginHandler()},
            {"/register", new AuthRegisterHandler()},
            {"/cancel", new CancelAccountHandler()},
    };
}
