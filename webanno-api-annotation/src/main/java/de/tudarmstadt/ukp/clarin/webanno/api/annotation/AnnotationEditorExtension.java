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
package de.tudarmstadt.ukp.clarin.webanno.api.annotation;

import java.io.IOException;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.wicket.ajax.AjaxRequestTarget;

import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;

import de.tudarmstadt.ukp.clarin.webanno.api.annotation.action.AnnotationActionHandler;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.VID;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.rendering.model.VDocument;

public interface AnnotationEditorExtension
{
    /**
     * @return get the bean name.
     */
    String getBeanName();

    /**
     * Handle an action.
     */
    default void handleAction(AnnotationActionHandler panel, AnnotatorState aState,
            AjaxRequestTarget aTarget, CAS aCas, VID paramId, String aAction)
        throws AnnotationException, IOException
    {
        // Do nothing by default
    }

    /**
     * Post-process the output during rendering.
     */
    default void render(CAS aCas, AnnotatorState aState, VDocument vdoc, int aWindowBeginOffset,
            int aWindowEndOffset)
    {
        // Do nothing by default
    }
    
    default void generateContextMenuItems(List<IMenuItem> aItems)
    {
        // Do nothing by default
    }
}
