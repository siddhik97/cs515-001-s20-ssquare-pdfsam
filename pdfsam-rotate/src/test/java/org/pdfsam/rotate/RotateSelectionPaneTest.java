/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 10/set/2014
 * Copyright 2017 by Sober Lemur S.a.s. di Vacondio Andrea (info@pdfsam.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.rotate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.pdfsam.eventstudio.StaticStudio.eventStudio;

import java.io.File;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.pdfsam.pdf.PdfDocumentDescriptor;
import org.pdfsam.pdf.PdfLoadRequestEvent;
import org.pdfsam.test.ClearEventStudioRule;
import org.pdfsam.test.InitializeAndApplyJavaFxThreadRule;
import org.sejda.conversion.exception.ConversionException;

/**
 * @author Andrea Vacondio
 *
 */
public class RotateSelectionPaneTest {

    private RotateSelectionPaneTestCore rotateSelectionPaneTestCore = new RotateSelectionPaneTestCore();

	public static final String MODULE = "MODULE";

    @Rule
    public ClearEventStudioRule clear = new ClearEventStudioRule(MODULE);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public InitializeAndApplyJavaFxThreadRule javaFxThread = new InitializeAndApplyJavaFxThreadRule();

    @Before
    public void setUp() {
        rotateSelectionPaneTestCore.setUp();
    }

    @Test
    public void empty() {
        rotateSelectionPaneTestCore.empty();
    }

    @Test
    public void emptyPageSelection() throws Exception {
        rotateSelectionPaneTestCore.emptyPageSelection(this);
    }

    @Test
    public void notEmptyPageSelection() throws Exception {
        rotateSelectionPaneTestCore.notEmptyPageSelection(this);
    }

    @Test
    public void converstionException() throws Exception {
        rotateSelectionPaneTestCore.converstionException(this);
    }

    @Test
    public void emptyByZeroPagesSelected() throws Exception {
        rotateSelectionPaneTestCore.emptyByZeroPagesSelected(this);
    }

    public void populate() throws Exception {
        File file = folder.newFile("temp.pdf");
        PdfLoadRequestEvent loadEvent = new PdfLoadRequestEvent(MODULE);
        loadEvent.add(PdfDocumentDescriptor.newDescriptorNoPassword(file));
        eventStudio().broadcast(loadEvent, MODULE);
    }
}
