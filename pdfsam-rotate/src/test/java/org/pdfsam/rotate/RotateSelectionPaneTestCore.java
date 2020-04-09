package org.pdfsam.rotate;


import java.util.function.Consumer;
import org.junit.Before;
import java.util.function.Consumer;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import java.util.Set;
import org.mockito.ArgumentCaptor;
import org.sejda.conversion.exception.ConversionException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.eq;

public class RotateSelectionPaneTestCore {
	private RotateParametersBuilder builder;
	private Consumer<String> onError;
	private RotateSelectionPane victim;

	@Before
	public void setUp() {
		builder = mock(RotateParametersBuilder.class);
		onError = mock(Consumer.class);
		victim = new RotateSelectionPane(RotateSelectionPaneTest.MODULE);
	}

	public void empty() {
		victim.apply(builder, onError);
		verify(onError).accept(anyString());
		verify(builder, never()).addInput(any(), any());
	}

	public void emptyPageSelection(RotateSelectionPaneTest rotateSelectionPaneTest) throws Exception {
		rotateSelectionPaneTest.populate();
		when(builder.hasInput()).thenReturn(Boolean.TRUE);
		victim.apply(builder, onError);
		verify(onError, never()).accept(anyString());
		ArgumentCaptor<Set> ranges = ArgumentCaptor.forClass(Set.class);
		verify(builder).addInput(any(), ranges.capture());
		assertTrue(ranges.getValue().isEmpty());
	}

	public void notEmptyPageSelection(RotateSelectionPaneTest rotateSelectionPaneTest) throws Exception {
		rotateSelectionPaneTest.populate();
		when(builder.hasInput()).thenReturn(Boolean.TRUE);
		victim.table().getItems().get(0).pageSelection.set("1,3-10");
		victim.apply(builder, onError);
		verify(onError, never()).accept(anyString());
		ArgumentCaptor<Set> ranges = ArgumentCaptor.forClass(Set.class);
		verify(builder).addInput(any(), ranges.capture());
		assertEquals(2, ranges.getValue().size());
	}

	public void converstionException(RotateSelectionPaneTest rotateSelectionPaneTest) throws Exception {
		rotateSelectionPaneTest.populate();
		doThrow(new ConversionException("message")).when(builder).addInput(any(), any());
		victim.apply(builder, onError);
		verify(builder).addInput(any(), any());
		verify(onError).accept(eq("message"));
	}

	public void emptyByZeroPagesSelected(RotateSelectionPaneTest rotateSelectionPaneTest) throws Exception {
		rotateSelectionPaneTest.populate();
		victim.table().getItems().get(0).pageSelection.set("0");
		victim.apply(builder, onError);
		verify(onError).accept(anyString());
		verify(builder, never()).addInput(any(), any());
	}

}