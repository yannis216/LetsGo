package com.example.android.letsgo.Utils;

import android.content.Context;
import android.util.Log;

import com.example.android.letsgo.Classes.Modul;
import com.example.android.letsgo.Classes.ModulElement;
import com.example.android.letsgo.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UsedForSorter {


    private Modul modul;
    private Context context;

    public UsedForSorter(Modul modul, Context context) {
        this.modul = modul;
        this.context = context;
    }

    public Map<String, Integer> getMostImportantUsedFor(){
        List<ModulElement> cModulElements = modul.getModulElements();
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();

        for(int i = 0; i<cModulElements.size(); i++){
            ModulElement cModulElement =cModulElements.get(i);
            Integer multiplier = cModulElement.getMultiplier().getTimesMultiplied();

            //UsedFors have more importance when done for Minutes or Hours instead of seconds
            String[] typeStrings = context.getResources().getStringArray(R.array.multiplier_type_array);
            if(cModulElement.getMultiplier().getType().equals(typeStrings[2])){
                multiplier = multiplier *60;
            }else if (cModulElement.getMultiplier().getType().equals(typeStrings[3])){
                multiplier = multiplier*60*60;
            }

            List<String> usedFors = cModulElement.getUsedFor();
            for(int ii = 0; ii<usedFors.size(); ii++){
                if(hashMap.containsKey(usedFors.get(ii))){
                    int value = hashMap.get(usedFors.get(ii));
                    int newValue = value + multiplier;
                    hashMap.put(usedFors.get(ii), newValue);
                }else{
                    hashMap.put(usedFors.get(ii), multiplier);
                }
            }
        }

        Map<String, Integer> sortedMap = sortByComparator(hashMap, false);

        Log.e("Hashmap", sortedMap.toString());
        return  sortedMap;
    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
