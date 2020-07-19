// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class FindMeetingQuery {

    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
       
        // Initialization
        ArrayList<TimeRange> TimeSegList = new ArrayList<TimeRange>();
        ArrayList<TimeRange> Answer = new ArrayList<TimeRange>();
        
        // Find all invalid time ranges
        for (Event nowEvent: events) {
            
            Set<String> eventAttendees = nowEvent.getAttendees();
            Collection<String> requestAttendees = request.getAttendees();
            boolean flag = false;

            for (String nowAtt: requestAttendees) {
                if (eventAttendees.contains(nowAtt)) {
                    flag = true;
                    break;
                }
            }

            if (flag == true) {
                TimeSegList.add(nowEvent.getWhen());
            }

            /* Here the use of retainAll seems to be wrong... but why?
            eventAttendees.retainAll(request.getAttendees());
            if (eventAttendees.size() > 0) {
                TimeSegList.add(nowEvent.getWhen());
            }
            */
        }

        // sort the time ranges by their start time
        Collections.sort(TimeSegList, TimeRange.ORDER_BY_START);

        // R: point to the latest available time
        int R = TimeRange.START_OF_DAY;
        for (TimeRange nowSeg: TimeSegList) {
            
            // if there's a valid time range, add into Answer
            if (nowSeg.start() > R && nowSeg.start() - R >= request.getDuration()) {
                Answer.add(TimeRange.fromStartEnd(R, nowSeg.start(), false));
            }

            // update R
            if (nowSeg.end() > R) {
                R = nowSeg.end();
            }
        }

        // add the last time range
        if (TimeRange.END_OF_DAY - R + 1 >= request.getDuration()) {
            Answer.add(TimeRange.fromStartEnd(R, TimeRange.END_OF_DAY, true));
        }

        return Answer;
    }
}
