/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2014 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU Affero General Public License for more details.  
 *  
 * You should have received a copy of the GNU Affero General Public License  
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.  
 */
package com.docdoku.server.rest;

import com.docdoku.core.exceptions.NotAllowedException;
import com.docdoku.core.exceptions.*;
import com.docdoku.core.security.UserGroupMapping;
import com.docdoku.core.services.IDocumentWorkflowManagerLocal;
import com.docdoku.core.services.IPartWorkflowManagerLocal;
import com.docdoku.core.workflow.ActivityKey;
import com.docdoku.core.workflow.TaskKey;
import com.docdoku.server.rest.dto.TaskProcessDTO;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Morgan Guimard
 */

@Stateless
@DeclareRoles(UserGroupMapping.REGULAR_USER_ROLE_ID)
@RolesAllowed(UserGroupMapping.REGULAR_USER_ROLE_ID)
public class TaskResource {

    @EJB
    private DocumentsResource documentsResource;
    @EJB
    private IDocumentWorkflowManagerLocal documentWorkflowService;
    @EJB
    private IPartWorkflowManagerLocal partWorkflowService;

    private static final Logger LOGGER = Logger.getLogger(TaskResource.class.getName());

    public TaskResource() {
    }

    @Path("{assignedUserLogin}/documents/")
    public DocumentsResource getDocumentsResource() {
        return documentsResource;
    }

    @POST
    @Path("documents/process")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response processTaskForDocuments(@PathParam("workspaceId") String workspaceId, @QueryParam("activityWorkflowId") int activityWorkflowId, @QueryParam("activityStep") int activityStep, @QueryParam("index") int index, @QueryParam("action") String action, TaskProcessDTO taskProcessDTO) {
        try {
            switch (action) {
                case "approve":
                    documentWorkflowService.approveTaskOnDocument(workspaceId, new TaskKey(new ActivityKey(activityWorkflowId, activityStep), index), taskProcessDTO.getComment(), taskProcessDTO.getSignature());
                    break;
                case "reject":
                    documentWorkflowService.rejectTaskOnDocument(workspaceId, new TaskKey(new ActivityKey(activityWorkflowId, activityStep), index), taskProcessDTO.getComment(), taskProcessDTO.getSignature());
                    break;
                default:
                    return Response.status(Response.Status.BAD_REQUEST).build();
            }

            return Response.ok().build();

        } catch (UserNotFoundException | NotAllowedException | UserNotActiveException e) {
            LOGGER.log(Level.WARNING, null, e);
            throw new RestApiException(e.toString(), e.getMessage(),Response.Status.FORBIDDEN);
        } catch (WorkspaceNotFoundException | WorkflowNotFoundException | TaskNotFoundException e) {
            LOGGER.log(Level.WARNING, null, e);
            throw new RestApiException(e.toString(), e.getMessage(),Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Path("parts/process")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response processTaskForParts(@PathParam("workspaceId") String workspaceId, @QueryParam("activityWorkflowId") int activityWorkflowId, @QueryParam("activityStep") int activityStep, @QueryParam("index") int index, @QueryParam("action") String action, TaskProcessDTO taskProcessDTO) {
        try {
            switch (action) {
                case "approve":
                    partWorkflowService.approveTaskOnPart(workspaceId, new TaskKey(new ActivityKey(activityWorkflowId, activityStep), index), taskProcessDTO.getComment(), taskProcessDTO.getSignature());
                    break;
                case "reject":
                    partWorkflowService.rejectTaskOnPart(workspaceId, new TaskKey(new ActivityKey(activityWorkflowId, activityStep), index), taskProcessDTO.getComment(), taskProcessDTO.getSignature());
                    break;
                default:
                    return Response.status(Response.Status.BAD_REQUEST).build();
            }

            return Response.ok().build();

        } catch (UserNotFoundException | NotAllowedException | UserNotActiveException e) {
            LOGGER.log(Level.WARNING, null, e);
            throw new RestApiException(e.toString(), e.getMessage(),Response.Status.FORBIDDEN);
        } catch (WorkspaceNotFoundException | TaskNotFoundException | WorkflowNotFoundException e) {
            LOGGER.log(Level.WARNING, null, e);
            throw new RestApiException(e.toString(), e.getMessage(),Response.Status.NOT_FOUND);
        }
    }
}