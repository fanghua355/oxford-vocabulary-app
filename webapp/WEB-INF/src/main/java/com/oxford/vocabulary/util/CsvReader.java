package com.oxford.vocabulary.util;

import com.oxford.vocabulary.model.Word;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CsvReader {
    private static final Logger logger = LoggerFactory.getLogger(CsvReader.class);

    public static List<Word> readOxford3000Words() {
        List<Word> words = new ArrayList<>();
        try {
            InputStream inputStream = CsvReader.class.getResourceAsStream("/data/oxford3000.csv");
            if (inputStream == null) {
                logger.error("Could not find oxford3000.csv in resources/data");
                return words;
            }

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(false)
                    .build();

            try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build()) {

                String[] line;
                while ((line = reader.readNext()) != null) {
                    Word word = new Word();
                    word.setWord(line[0].trim());
                    word.setPhonetic(line[1].trim());
                    word.setTranslation(line[2].trim());
                    word.setLevel(line[3].trim());
                    word.setExample(line[4].trim());
                    word.setPartOfSpeech(line[5].trim());
                    word.setDefinition(line[6].trim());

                    Set<String> synonyms = new HashSet<>();
                    if (line.length > 7 && !line[7].trim().isEmpty()) {
                        String[] synonymArray = line[7].split(";");
                        synonyms.addAll(Arrays.asList(synonymArray));
                    }
                    word.setSynonyms(synonyms);

                    Set<String> antonyms = new HashSet<>();
                    if (line.length > 8 && !line[8].trim().isEmpty()) {
                        String[] antonymArray = line[8].split(";");
                        antonyms.addAll(Arrays.asList(antonymArray));
                    }
                    word.setAntonyms(antonyms);

                    words.add(word);
                }
            }
            logger.info("Read {} words from CSV file", words.size());
        } catch (Exception e) {
            logger.error("Error reading CSV file", e);
        }
        return words;
    }
} 