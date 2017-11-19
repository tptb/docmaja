package com.docmala.parser;

import com.docmala.Error;
import com.docmala.parser.blockParsers.*;

import java.io.IOException;
import java.util.ArrayDeque;

public class Parser {
    Document document;
    ArrayDeque<Error> errors = new ArrayDeque<>();
    CommentParser commentParser = new CommentParser();
    HeadlineParser headlineParser = new HeadlineParser();
    ListParser listParser = new ListParser();
    PluginParser pluginParser;
    ContentParser contentParser = new ContentParser();

    public Parser() {
    }

    public ArrayDeque<Error> errors() {
        return errors;
    }

    public void parse(ISourceProvider sourceProvider, String fileName) throws IOException {
        document = new Document();

        ISourceProvider thisSourceProvider = sourceProvider.subProvider(fileName);
        String thisFileName = sourceProvider.getFileName(fileName);
        ISource source = thisSourceProvider.get(thisFileName);

        pluginParser = new PluginParser(thisSourceProvider);

        ISource.Window window = source.begin();

        while (!window.here().isEof()) {
            while (window.here().isNewLine())
                window.moveForward();

            if (commentParser.tryParse(window, document)) {
                errors.addAll(commentParser.errors());
                continue;
            } else if (headlineParser.tryParse(window, document)) {
                errors.addAll(headlineParser.errors());
                continue;
            } else if (listParser.tryParse(window, document)) {
                errors.addAll(listParser.errors());
                continue;
            } else if (pluginParser.tryParse(window, document)) {
                errors.addAll(pluginParser.errors());
                continue;
            }

            contentParser.tryParse(window, document);

        }
    }

    public Document document() {
        return document;
    }
}
