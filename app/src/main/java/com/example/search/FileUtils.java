package com.example.search;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String DIRECTORY_NAME = "search_items";
    private static final String FILE_EXTENSION = ".txt";

    /**
     * Save a SearchItem to internal storage
     * @param context Application context
     * @param item SearchItem to save
     * @return true if successful, false otherwise
     */
    public static boolean saveItem(Context context, SearchItem item) {
        // Create directory if it doesn't exist
        File directory = new File(context.getFilesDir(), DIRECTORY_NAME);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                Log.e(TAG, "Failed to create directory");
                return false;
            }
        }

        // Create file name based on item ID
        String fileName = "item_" + item.getId() + FILE_EXTENSION;
        File file = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Format: ID|Title|Description
            String content = item.getId() + "|" + item.getTitle() + "|" + item.getDescription();
            fos.write(content.getBytes());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error saving file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load a SearchItem from internal storage
     * @param context Application context
     * @param itemId ID of the SearchItem to load
     * @return SearchItem or null if not found
     */
    public static SearchItem loadItem(Context context, int itemId) {
        File directory = new File(context.getFilesDir(), DIRECTORY_NAME);
        String fileName = "item_" + itemId + FILE_EXTENSION;
        File file = new File(directory, fileName);

        if (!file.exists()) {
            Log.d(TAG, "File does not exist: " + fileName);
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String content = reader.readLine();
            if (content != null) {
                String[] parts = content.split("\\|");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    String title = parts[1];
                    String description = parts[2];
                    return new SearchItem(id, title, description);
                }
            }
        } catch (IOException | NumberFormatException e) {
            Log.e(TAG, "Error loading file: " + e.getMessage());
        }

        return null;
    }

    /**
     * Delete a saved SearchItem
     * @param context Application context
     * @param itemId ID of the SearchItem to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteItem(Context context, int itemId) {
        File directory = new File(context.getFilesDir(), DIRECTORY_NAME);
        String fileName = "item_" + itemId + FILE_EXTENSION;
        File file = new File(directory, fileName);

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * List all saved items
     * @param context Application context
     * @return List of saved SearchItems
     */
    public static List<SearchItem> getAllSavedItems(Context context) {
        List<SearchItem> items = new ArrayList<>();
        File directory = new File(context.getFilesDir(), DIRECTORY_NAME);

        if (!directory.exists()) {
            return items;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(FILE_EXTENSION)) {
                    try (FileInputStream fis = new FileInputStream(file);
                         BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

                        String content = reader.readLine();
                        if (content != null) {
                            String[] parts = content.split("\\|");
                            if (parts.length == 3) {
                                int id = Integer.parseInt(parts[0]);
                                String title = parts[1];
                                String description = parts[2];
                                items.add(new SearchItem(id, title, description));
                            }
                        }
                    } catch (IOException | NumberFormatException e) {
                        Log.e(TAG, "Error reading file: " + e.getMessage());
                    }
                }
            }
        }

        return items;
    }
}