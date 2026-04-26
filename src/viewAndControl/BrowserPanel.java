package viewAndControl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Abstract base panel for browsing either games or collections.
 * Defines the overall layout algorithm: header, grid, and filter dropdown.
 *
 * Design Pattern:
 * Template Method
 * This class defines the skeleton of the
 * browser UI in buildHeader() and buildGrid(). Subclasses implement the
 * primitive operations getTitle(), getSearchHint(), and buildCard() to
 * provide type-specific behavior without changing the overall structure.
 */
public abstract class BrowserPanel extends JPanel
{
    protected JPanel gridPanel;
    protected JScrollPane scrollPane;
    private JLabel titleLabel;
    private JButton filterButton;
    public abstract JPopupMenu buildFilterPanel();

    /**
     * Constructs the browser panel by initializing layout,
     * then building the header and grid in order.
     */
    public BrowserPanel()
    {
        this.setLayout(new BorderLayout());
        this.setBackground(GUIColors.MID);
        buildHeader();
    }
    /**
     * Returns the placeholder text displayed in the search field.
     * @return the search hint string
     */
    public abstract String getSearchHint();
    /**
     * Builds and returns a single card panel for the given index.
     * @param index the position of the card in the grid
     * @return a JPanel representing one game or collection card
     */
    public abstract JPanel buildCard(int index);
    /**
     * Builds the header panel containing the title, search bar, and filter button.
     */
    protected void buildHeader()
    {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(GUIColors.DARK);
        headerPanel.setPreferredSize(new Dimension(50, 80)); // taller for two rows
        headerPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        //top row: title on left hand side and search on right hand side
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(GUIColors.DARK);

        titleLabel = new JLabel(getTitle());
        titleLabel.setFont(new Font("Arial Black", Font.BOLD,22));
        titleLabel.setForeground(GUIColors.LIGHT);
        //build search field
        JTextField searchField = new JTextField(getSearchHint());
        searchField.setFont(new Font("Arial Black", Font.PLAIN, 13));
        searchField.setBackground(GUIColors.CREAM);
        searchField.setForeground(GUIColors.DARK);
        searchField.setPreferredSize(new Dimension(220, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GUIColors.DARK, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        //placeholder for search field
        searchField.addFocusListener(new java.awt.event.FocusAdapter()
        {
            @Override
            public void focusGained(java.awt.event.FocusEvent e)
            {
                //when user clicks into the field remove the placeholder
                if (searchField.getText().equals(getSearchHint()))
                {
                    searchField.setText("");
                    searchField.setForeground(GUIColors.DARK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e)
            {
                //if the user leaves the field empty return to placeholder
                if(searchField.getText().isEmpty())
                {
                    searchField.setText(getSearchHint());
                    searchField.setForeground(Color.BLACK);
                }
            }
        });
        //when the user starts to type, being search filtering(detect inserting,deleting)
        //it will monitor text changes and filter cards
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener()
        {
            //insertUpdate, removeUpdate, and changedUpdate will handle any text changes that occur
            public void insertUpdate(javax.swing.event.DocumentEvent e)
            {
                handleSearch();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e)
            {
                handleSearch();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e)
            {
                handleSearch();
            }

            /**
             * Extracts current search text and triggers filtering.
             * Ignores the placeholder text hint
             */
            private void handleSearch()
            {
                String text = searchField.getText();
                //if the search has real text pass it to onSearch to begin filter
                if(!text.equals(getSearchHint()))
                {
                    onSearch(text);
                }
                //if the field shows the placeholder treat it as empty and clear filtering
                else
                {
                    onSearch("");
                }
            }
        });

        topRow.add(titleLabel, BorderLayout.NORTH);
        topRow.add(searchField, BorderLayout.EAST);

        //bottom row: filter by button on the left hand side
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomRow.setBackground(GUIColors.DARK);

        //build filter button
        filterButton = new JButton("Filter");
        filterButton.setFont(new Font("Arial Black", Font.PLAIN, 13));
        filterButton.setBackground(GUIColors.CREAM);
        filterButton.setForeground(GUIColors.DARK);
        filterButton.setPreferredSize(new Dimension(110, 28));
        filterButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GUIColors.DARK, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        filterButton.setFocusPainted(true);

        filterButton.addActionListener(event ->
        {
            buildFilterPanel().show(filterButton, 0 , filterButton.getHeight());
        });

        bottomRow.add(filterButton);

        headerPanel.add(topRow, BorderLayout.NORTH);
        headerPanel.add(bottomRow, BorderLayout.SOUTH);

        this.add(headerPanel, BorderLayout.NORTH);
    }
    /**
     * Builds the scrollable grid of cards and adds it to the center of the panel.
     */
    protected void buildGrid()
    {
        gridPanel = new JPanel(new GridLayout(0,getColumnCount(),10,10));
        gridPanel.setBackground(GUIColors.MID);
        gridPanel.setBorder(new EmptyBorder(10,10,10,10));

        for (int i = 0; i < getCardCount(); i++)
        {
            gridPanel.add(buildCard(i));
        }

        // wrapper stops the grid from stretching vertically
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        wrapper.setBackground(GUIColors.MID);
        wrapper.add(gridPanel);

        scrollPane=new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI()
        {
            @Override
            protected void configureScrollBarColors()
            {
                this.thumbColor = GUIColors.DARK;
                this.trackColor = GUIColors.LIGHT;
            }
        });
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane,BorderLayout.CENTER);
    }
    /**
     * Handles search inputs and updates cards accordingly.
     *
     * @param query current search string, null if cleared
     */
    public abstract void onSearch(String query);
    /**
     * Returns the total number of cards for grid to display.
     *
     * @return total card count
     */
    public abstract int getCardCount();
    /**
     * Refreshes grid whenever screen is resized and updates data to reflect changes.
     */
    public void refresh()
    {
        if(scrollPane != null) this.remove(scrollPane);
        buildGrid();
        revalidate();
        repaint();
    }
    /**
     * Returns the title displayed in the panel header.
     * @return the panel title string
     */
    public abstract String getTitle();
    /**
     * Updates the title label in the header to passed string.
     *
     * @param title the new title string to display
     */
    public void updateTitle(String title)
    {
        titleLabel.setText(title);
    }
    /**
     * Determines whether filter button should be visible.
     * Override in classes that don't support filtering
     *
     * @return true if the filler button should be shown
     */
    protected boolean showFilterButton()
    {
        return true;
    }
    /**
     * Shows or hides the filter button based on the subclass.
     *
     */
    public void updateFilterButton()
    {
        if (filterButton != null)
            filterButton.setVisible(showFilterButton());
    }
    /**
     * Returns the number of columns based on panel width.
     *
     * @return column count, minimum of 1
     */
    private int getColumnCount()
    {
        int width = getWidth();
        if (width == 0) return 4;
        return Math.max(1,width / 180);
    }
    /**
     * Shrinks strings size if it passes a certain threshold.
     *
     * @param text to test length
     * @return threshold meeting string
     */
    protected String truncate(String text)
    {
        if(text.length() > 18)
        {
            return text.substring(0,18) + "...";
        }
        return text;
    }

}