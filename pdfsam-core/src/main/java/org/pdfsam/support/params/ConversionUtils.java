/*
 * This file is part of the PDF Split And Merge source code
 * Created on 22 giu 2016
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
package org.pdfsam.support.params;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sejda.conversion.AdapterUtils.splitAndTrim;

import java.util.Collections;
import java.util.Set;

import org.pdfsam.i18n.DefaultI18nContext;
import org.sejda.commons.collection.NullSafeSet;
import org.sejda.conversion.exception.ConversionException;
import org.sejda.model.pdf.page.PageRange;


public final class ConversionUtils {
    //private static final Logger LOG = LoggerFactory.getLogger(ConversionUtils.class);

    private ConversionUtils() {
        // hide
    }

    public static Set<PageRange> toPageRangeSet(String selection) throws ConversionException {
       // LOG.info("pdfsam-core: support: param: conversionUtils:: BUG::: selection = {}",selection);
        if (isNotBlank(selection)) {
            Set<PageRange> pageRangeSet = new NullSafeSet<>();
            String[] tokens = splitAndTrim(selection, ",");
            for (String current : tokens) {
               // LOG.info("pdfsam-core: support: param: conversionUtils:: BUG::: current page= {}",current);
                PageRange range = toPageRange(current);
                //LOG.info("pdfsam-core: support: param: conversionUtils:: BUG::: range start = {}; end={}, range={}", range.getStart(), range.getEnd(),range.toString());
                if (range.getEnd() < range.getStart()) {
                    throw new ConversionException(
                            DefaultI18nContext.getInstance().i18n("Invalid range: {0}.", range.toString()));
                }
                /* TODO- Check if the range already present in the pageRangeSet */
                if (!pageRangeSet.isEmpty()) {
                    PageRange tmpRange = range;
                    boolean update = true;
                    for (PageRange item : pageRangeSet) {
                        update = true;
                        // 1-5 present | new 2-6
                        if (tmpRange.getEnd() <= item.getEnd()) {
                            update = false;
                            continue;
                        }
                        // 1-8 present | new 2-20
                        if (tmpRange.getStart() <= item.getEnd()) {
                            // Present 1-4| new 5
                            if (tmpRange.getStart() == tmpRange.getEnd()) {
                                update = false;
                                continue;
                            }
                            // Present 1-8,9-40,50 | new 30-80
                            // Remove 50 as it is included in 41-80
                            if (item.getStart() == item.getEnd() && item.getStart() >=tmpRange.getStart() && item.getEnd() <=tmpRange.getEnd()){
                                pageRangeSet.remove(item);
                               // LOG.info("pdfsam-core: support: param: conversionUtils:: BUG::: REMOVED item={}; tmprange={}",item.toString(),tmpRange.toString());

//                                if (tmpRange.getStart() == range.getStart() &&  tmpRange.getEnd() == range.getEnd()){
//                                    update=false;
//                                }
                                continue;
                            }

                            int tmpStart = tmpRange.getStart() + 1;
                            while (tmpStart <= item.getEnd()) {
                                tmpStart++;
                            }
                            if (tmpStart > tmpRange.getEnd()) {
                                update = false;
                                continue;
                            }
                            tmpRange = toPageRange(tmpStart + "-" + tmpRange.getEnd());
                        }

                    }
                    //LOG.info("pdfsam-core: support: param: conversionUtils:: BUG::: SKIP-CHECK range added = {}; update={}",tmpRange,update);
                    if (update) {
                        pageRangeSet.add(tmpRange);
                    }
                }else {
                   // LOG.info("pdfsam-core: support: param: conversionUtils:: BUG::: else range added = {}",range);
                    pageRangeSet.add(range);
                }
                for(PageRange item:pageRangeSet){
                   // LOG.info("pdfsam-core: support: param: conversionUtils:: BUG::: Final range = {}",item.toString());

                }
//                pageRangeSet.add(range);
//                LOG.info("pdfsam-core: support: param: conversionUtils:: BUG::: range added = {}",range);
            }
            return pageRangeSet;
        }
        return Collections.emptySet();
    }

    private static PageRange toPageRange(String value) throws ConversionException {
        String[] limits = splitAndTrim(value, "-");
        if (limits.length > 2) {
            throw new ConversionException(DefaultI18nContext.getInstance().i18n(
                    "Ambiguous page range definition: {0}. Use following formats: [n] or [n1-n2] or [-n] or [n-]",
                    value));
        }
        if (limits.length == 1) {
            int limitNumber = parsePageNumber(limits[0]);
            if (value.endsWith("-")) {
                return new PageRange(limitNumber);
            }
            if (value.startsWith("-")) {
                return new PageRange(1, limitNumber);
            }
            return new PageRange(limitNumber, limitNumber);
        }
        return new PageRange(parsePageNumber(limits[0]), parsePageNumber(limits[1]));
    }

    private static int parsePageNumber(String value) throws ConversionException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException nfe) {
            throw new ConversionException(DefaultI18nContext.getInstance().i18n("Invalid number: {0}.", value));
        }
    }
}
