package chelpereclipse.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardUrlPage extends WizardPage {
	private static final String TITLE = "Enter problem URL";
	private static final String DESCRIPTION = "Enter problem URL";
	
	private Text url;
	private Composite container;
	
	public WizardUrlPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		
		layout.numColumns = 2;
		Label urlLabel = new Label(container, SWT.NONE);
		urlLabel.setText("URL: ");
		url = new Text(container, SWT.NONE);
		url.setText("http://codechef.com");
		url.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) { }
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(!url.getText().isEmpty()) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		url.setLayoutData(gd);
		setControl(container);
		setPageComplete(true);
	}
	
	public String getUrl() {
		return url.getText();
	}
}
