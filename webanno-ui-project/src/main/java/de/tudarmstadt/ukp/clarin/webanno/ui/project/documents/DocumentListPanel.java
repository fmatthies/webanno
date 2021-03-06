/*
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.clarin.webanno.ui.project.documents;

import java.io.IOException;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.support.dialog.ConfirmationDialog;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxButton;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaModel;

public class DocumentListPanel
    extends Panel
{
    private static final long serialVersionUID = -4981251367691500470L;

    private static final Logger LOG = LoggerFactory.getLogger(DocumentListPanel.class);

    private @SpringBean DocumentService documentService;

    private ListMultipleChoice<SourceDocument> overviewList;
    private IModel<Project> project;
    private CollectionModel<SourceDocument> selectedDocuments;
    private ConfirmationDialog confirmationDialog;
    
    public DocumentListPanel(String aId, IModel<Project> aProject)
    {
        super(aId);
     
        setOutputMarkupId(true);
        
        project = aProject;
        selectedDocuments = new CollectionModel<>();

        Form<Void> form = new Form<>("form");
        add(form);
        
        overviewList = new ListMultipleChoice<>("documents");
        overviewList.setChoiceRenderer(new ChoiceRenderer<>("name"));
        overviewList.setModel(selectedDocuments);
        overviewList.setChoices(LambdaModel.of(this::listSourceDocuments));
        form.add(overviewList);

        confirmationDialog = new ConfirmationDialog("confirmationDialog");
        confirmationDialog.setTitleModel(new StringResourceModel("DeleteDialog.title", this));
        add(confirmationDialog);

        form.add(new LambdaAjaxButton<>("delete", this::actionDelete));
    }
    
    private List<SourceDocument> listSourceDocuments()
    {
        return documentService.listSourceDocuments(project.getObject());
    }
    
    private void actionDelete(AjaxRequestTarget aTarget, Form<Void> aForm)
    {
        if (selectedDocuments.getObject() == null || selectedDocuments.getObject().isEmpty()) {
            error("No documents selected");
            aTarget.addChildren(getPage(), IFeedback.class);
            return;
        }
        
        confirmationDialog.setContentModel(new StringResourceModel("DeleteDialog.text", this)
                .setParameters(selectedDocuments.getObject().size()));
        confirmationDialog.show(aTarget);
        
        confirmationDialog.setConfirmAction((_target) -> {
            for (SourceDocument sourceDocument : selectedDocuments.getObject()) {
                try {
                    documentService.removeSourceDocument(sourceDocument);
                }
                catch (IOException e) {
                    LOG.error("Unable to delete document", e);
                    error("Unable to delete document: " + e.getMessage());
                    _target.addChildren(getPage(), IFeedback.class);
                }
            }
            selectedDocuments.getObject().clear();
            _target.add(getPage());
        });
    }
}
