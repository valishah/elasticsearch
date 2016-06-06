/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.rest.action.admin.indices;

import org.elasticsearch.action.admin.indices.rollover.RolloverRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.support.RestToXContentListener;

/**
 *
 */
public class RestRolloverIndexAction extends BaseRestHandler {

    @Inject
    public RestRolloverIndexAction(Settings settings, RestController controller, Client client) {
        super(settings, client);
        controller.registerHandler(RestRequest.Method.POST, "/{alias}/_rollover", this);
        controller.registerHandler(RestRequest.Method.GET, "/{alias}/_rollover", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, final Client client) {
        if (request.param("alias") == null) {
            throw new IllegalArgumentException("no source alias");
        }
        RolloverRequest rolloverIndexRequest = new RolloverRequest(request.param("alias"));
        if (request.hasContent()) {
            rolloverIndexRequest.source(request.content());
        }
        rolloverIndexRequest.simulate(request.method() == RestRequest.Method.GET || request.paramAsBoolean("simulate", false));
        rolloverIndexRequest.timeout(request.paramAsTime("timeout", rolloverIndexRequest.timeout()));
        rolloverIndexRequest.masterNodeTimeout(request.paramAsTime("master_timeout", rolloverIndexRequest.masterNodeTimeout()));
        client.admin().indices().rolloverIndex(rolloverIndexRequest, new RestToXContentListener<>(channel));
    }
}