package test.org.ei.action;

import javax.servlet.http.HttpServletRequest;

import org.ei.controller.DataResponse;
import org.ei.controller.logging.LogEntry;
import org.ei.session.UserSession;

public class MockLogEntry extends LogEntry {

    @Override
    public LogEntry addDataResponse(DataResponse dataResponse) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public LogEntry addUserSession(UserSession usersession) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public LogEntry addHttpData(HttpServletRequest request) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public void enqueue() {
        // When unit testing this should do nothing!
        return;
    }

}
