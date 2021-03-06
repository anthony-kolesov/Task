package org.plugin.task.editors;


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.ide.IDE;
import org.plugin.task.editors.Info;
/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener
{

	/** The text editor used in page 0. */
	private TextEditor editor;

	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPageEditor() 
	{
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	/**
	 * Creates page witch allows you to fill processor data and save it
	 */
	void createPage() 
	{
        //TODO make page scrollable
		Composite mainComposite = new Composite(getContainer(), SWT.NONE);
		GridLayout oneColumnLayout = new GridLayout(1, false);
		mainComposite.setLayout(oneColumnLayout);
		
		
		//Title
		Composite titleComposite = new Composite(mainComposite, SWT.NONE);
		titleComposite.setLayout(oneColumnLayout);
		Label titleLabel = new Label(titleComposite, SWT.NONE);
		titleLabel.setText("Title:");
		Text titleText = new Text(titleComposite, SWT.NONE);
		
		//Memory Label
		Composite memoryLabelComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout doubleColumnLayout = new GridLayout(2, false);
		memoryLabelComposite.setLayout(doubleColumnLayout);
		Label memoryLabel = new Label(memoryLabelComposite, SWT.NONE);
		memoryLabel.setText("Memory (name, origin, length):"); 
		Label questionLabel = new Label(memoryLabelComposite, SWT.NONE);
		questionLabel.setText(" ?"); 
		questionLabel.setToolTipText("Origin must be in decimal or hexadecimal (like 0x00000000) format");
		
		//Memory fields
		Composite memoryComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout tripleColumnLayout = new GridLayout(3, false);
		memoryComposite.setLayout(tripleColumnLayout);
		List<Text[]> memoryTFs = new ArrayList<Text[]>(); 
		Text name = new Text(memoryComposite, SWT.NONE);
		Text origin = new Text(memoryComposite, SWT.NONE);
		Text length = new Text(memoryComposite, SWT.NONE);
		memoryTFs.add(new Text[] { name, origin, length });
		
		//Plus button
		Composite memoryButtonComposite = new Composite(mainComposite, SWT.NONE);
		memoryButtonComposite.setLayout(doubleColumnLayout);
		Button plusButton = new Button(memoryButtonComposite, SWT.PUSH);
		plusButton.setText("+");
		//TODO Button minusButton = new Button(memoryButtonComposite, SWT.PUSH);
		// minusButton.setText("-");
		
		//Aliases
		Composite aliasComposite = new Composite(mainComposite, SWT.NONE); 
		aliasComposite.setLayout(doubleColumnLayout);		
		Label startupLabel = new Label(aliasComposite, SWT.NONE);
		startupLabel.setText("Startup:");
		Combo startupCombo = new Combo(aliasComposite, SWT.DRAG | SWT.ARROW_DOWN | SWT.READ_ONLY);
		startupCombo.addFocusListener(setComboListener(startupCombo, aliasComposite, memoryTFs));
		Label textLabel = new Label(aliasComposite, SWT.NONE);
		textLabel.setText("Text:");
		Combo textCombo = new Combo(aliasComposite, SWT.DRAG | SWT.ARROW_DOWN | SWT.READ_ONLY);
		textCombo.addFocusListener(setComboListener(textCombo, aliasComposite, memoryTFs));	
		Label dataLabel = new Label(aliasComposite, SWT.NONE);
		dataLabel.setText("Data:");
		Combo dataCombo = new Combo(aliasComposite, SWT.DRAG | SWT.ARROW_DOWN | SWT.READ_ONLY);
		dataCombo.addFocusListener(setComboListener(dataCombo, aliasComposite, memoryTFs));	
		Label sdataLabel = new Label(aliasComposite, SWT.NONE);
		sdataLabel.setText("Sdata:");
		Combo sdataCombo = new Combo(aliasComposite, SWT.DRAG | SWT.ARROW_DOWN | SWT.READ_ONLY);
		sdataCombo.addFocusListener(setComboListener(sdataCombo, aliasComposite, memoryTFs));
		
		//Predefined characters
		Composite predefineCharComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout predefinedCharLayout = new GridLayout(3, false);
		predefineCharComposite.setLayout(predefinedCharLayout);
		Label stackTopLabel = new Label(predefineCharComposite, SWT.NONE);
		stackTopLabel.setText("__end_heap:");
		Text stackTopText = new Text(predefineCharComposite, SWT.NONE);		  
		Label additionalLabel = new Label(predefineCharComposite, SWT.NONE);
		additionalLabel.setText("&& -4");
		Label endHeapLabel = new Label(predefineCharComposite, SWT.NONE);
		endHeapLabel.setText("__stack_top:");
		Text endHeapText = new Text(predefineCharComposite, SWT.NONE);
		Label questionLabel1 = new Label(predefineCharComposite, SWT.NONE);
		questionLabel1.setText(" ?");
		questionLabel1.setToolTipText("Precharecters must be in decimal or hexadecimal (like 0x00000000) format");	
		
		//Save button & message label
		Composite saveComposite = new Composite(mainComposite, SWT.NONE);
		saveComposite.setLayout(oneColumnLayout);
		Button saveButton = new Button(saveComposite, SWT.NONE);
		GridData SaveGD = new GridData(GridData.BEGINNING);
		SaveGD.horizontalSpan = 1;
		saveButton.setLayoutData(SaveGD);
		saveButton.setText("Save file");
		Label messageLabel = new Label(saveComposite, SWT.NONE);
		messageLabel.setText("");
		
		//Listeners
		plusButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
	    		Text name = new Text(memoryComposite, SWT.NONE);
	    		Text origin = new Text(memoryComposite, SWT.NONE);
	    		Text length = new Text(memoryComposite, SWT.NONE);
	    		
	    		memoryComposite.requestLayout();
	    		memoryTFs.add(new Text[] { name, origin, length });
			}
		});		
		stackTopText.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent arg0) 
			{
				endHeapText.setText(stackTopText.getText());
				saveComposite.layout();
				saveComposite.pack(); 
			}
		});		
		saveButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				messageLabel.setText("");
				saveComposite.layout();
				saveComposite.pack();
				Info info = new Info();
					try 
					{
						info = CreateInfo();
						if (info.CheckData())
						{
							
							info.setFileText(info.InfoToList());
							info.SaveFile();
						}
						else 
						{
							throw new Exception(info.getErrorText());
						}
					} 
					catch (Exception ex) 
					{
						if (info.getErrorText()!="")
						{
							messageLabel.setText(info.getErrorText());
							info.clearErrorText();
						}
						else
						{
							messageLabel.setText(ex.getMessage());
						}
						saveComposite.layout();
						saveComposite.pack();
					}
			}
			
			public Info CreateInfo()
			{
				try
				{
					Info info = new Info();
					info.setTitle(titleText.getText());
					info.Memory = new HashMap<String, String[]>();
					for(Text[] memoryInfo: memoryTFs)
					{
						String name = 	((Text) memoryInfo[0]).getText();
						String[] originAndLength = new String[] 
								{memoryInfo[1].getText(), memoryInfo[2].getText()};
						info.Memory.put(name, originAndLength);
					}
					info.setAliases(new String[] 
						{ startupCombo.getText(), textCombo.getText(), 
								dataCombo.getText(), sdataCombo.getText()});
					info.setStackTop(stackTopText.getText());
					info.setEndHeap(endHeapText.getText());
					return info;
				}
				catch(Exception ex)
				{
					throw ex;
				}
			}
			
		});
		
		int index = addPage(mainComposite);
		setPageText(index, "Task");
	}
	
	private FocusListener setComboListener(Combo combo, Composite combocomposite, List<Text[]> memoryTFs)
	{
		FocusListener comboListener = new FocusListener() {
		      public void focusGained(FocusEvent e) {
		    	  combo.removeAll();
		    	  for (Text[] memoryInfo: memoryTFs)
		    	  {
		    		    String name = memoryInfo[0].getText();
						combo.add(name); 
						combocomposite.layout();
						combocomposite.pack();
		    	  }
		      }

			@Override
			public void focusLost(FocusEvent e) {				
			}
		};
		return comboListener;
	}
	
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() 
	{
		createPage();
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() 
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) 
	{
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() 
	{
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) 
	{
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException 
	{
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() 
	{
		return true;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) 
	{
		super.pageChange(newPageIndex);
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event)
	{
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++)
					{
						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource()))
						{
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
}
