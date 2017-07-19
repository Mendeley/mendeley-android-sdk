package com.mendeley.sdk.request;

import android.net.Uri;

import org.json.JSONObject;

import java.io.InputStream;

import static com.mendeley.sdk.util.IOUtils.readInputStream;

public class GetAuthorizedRequestTest extends AuthorizedRequestTest {

    @Override
    protected AuthorizedRequest<JSONObject> createRequest() {
        return new GetAuthorizedRequest<JSONObject>(Uri.parse("https://httpbin.org/get"), getAuthManager(), getClientCredentials()) {
            @Override
            protected JSONObject manageResponse(InputStream is) throws Exception {
                String responseString = readInputStream(is);
                return new JSONObject(responseString);
            }
        };
    }

//    @FlakyTest
//    public void test_cancel_interruptsReadingFromTheInputStream() throws InterruptedException, MendeleyException {
//        // GIVEN a request
//        final Request<Integer> request = new GetAuthorizedRequest<Integer>(Uri.parse("http://mirror.internode.on.net/pub/test/5meg.test1"), getAuthManager(), getClientCredentials()) {
//            @Override
//            protected Integer manageResponse(InputStream is) throws Exception {
//                int total = 0;
//                byte[] buffer = new byte[1024 * 16];
//                int r;
//                while ((r = is.read(buffer)) > -1) {
//                    total += r;
//                }
//
//                return total;
//            }
//        };
//
//        // WHEN running and cancelling it
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
//                request.cancel();
//            }
//        }).start();
//
//
//        Integer resource = null;
//        UserCancelledException userCancelledException = null;
//        try {
//            resource = request.run().resource;
//        } catch (UserCancelledException e) {
//            // fine
//            userCancelledException = e;
//        } catch (Exception e) {
//            throw e;
//        }
//
//        // THEN we have received a cancelled exception
//        assertNull("resource should be null", resource);
//        assertNotNull("cancellation exception should be received", userCancelledException);
//    }

}
