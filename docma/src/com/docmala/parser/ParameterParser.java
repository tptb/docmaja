package com.docmala.parser;

import com.docmala.Error;
import com.docmala.Parameter;

import java.util.ArrayDeque;

public class ParameterParser {
    Parameter _parameter;

    public ArrayDeque<Error> errors() {
        return _errors;
    }

    protected ArrayDeque<Error> _errors;

    public Parameter parameter() {
        return _parameter;
    }

    public Source.Window parse(Source.Window start, char[] end) {
        _errors = new ArrayDeque<>();
        start.skipWhitspaces();
        Source.Position begin = start.here();
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();

        char[] endPlusEquals = new char[end.length + 1];
        System.arraycopy(end, 0, endPlusEquals, 0, end.length);
        endPlusEquals[end.length] = '=';

        while (!start.here().isBlockEnd()) {
            if (start.here().equals(endPlusEquals)) {
                break;
            }
            name.append(start.here().get());
            start.moveForward();
        }

        if (start.here().equals('=')) {
            start.moveForward();
            start.skipWhitspaces();

            boolean searchQuotationMark = start.here().equals('"');
            boolean isQuotedParameter = searchQuotationMark;
            if( searchQuotationMark ) {
                start.moveForward();
            }

            while (!start.here().isBlockEnd()) {
                if (searchQuotationMark && start.here().equals('"') ) {
                    searchQuotationMark = false;
                    start.moveForward();
                    start.skipWhitspaces();
                    if( !start.here().equals(end) ) {
                        _errors.addLast(new Error(start.here(), "Expected: " + end));
                    }
                    break;
                }
                if( !searchQuotationMark && start.here().equals(end)) {
                    break;
                }
                value.append(start.here().get());
                start.moveForward();
            }
            if( searchQuotationMark ) {
                _errors.addLast(new Error(start.here(), "Expected: '\"'"));
            }
        }

        _parameter = new Parameter(name.toString().trim(), value.toString(), begin);
        return start;
    }
}