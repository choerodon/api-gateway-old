package io.choerodon.gateway.my

import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand
import org.springframework.http.client.ClientHttpResponse
import rx.Observable

import java.util.concurrent.Future

/**
 * Created by superlee on 2018/9/18.
 */
class MyRibbonCommand implements RibbonCommand {

    private MyClientHttpResponse myClientHttpResponse

    MyRibbonCommand() {}

    MyRibbonCommand(MyClientHttpResponse myClientHttpResponse) {
        this.myClientHttpResponse = myClientHttpResponse
    }
    @Override
    ClientHttpResponse execute() {
        return myClientHttpResponse
    }

    @Override
    Future<ClientHttpResponse> queue() {
        return null
    }

    @Override
    Observable<ClientHttpResponse> observe() {
        return null
    }
}
