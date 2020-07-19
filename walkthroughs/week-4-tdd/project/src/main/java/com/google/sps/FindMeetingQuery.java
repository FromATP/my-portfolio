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
    ArrayList<TimeRange> timeSegList = new ArrayList<TimeRange>();
    ArrayList<TimeRange> answer = new ArrayList<TimeRange>();
        
    // Find all invalid time ranges
    for (Event nowEvent : events) {
            
      Set<String> eventAttendees = nowEvent.getAttendees();
      Collection<String> requestAttendees = request.getAttendees();
      boolean flag = false;

      for (String nowAtt : requestAttendees) {
        if (eventAttendees.contains(nowAtt)) {
          flag = true;
          break;
        }
      }

      if (flag) {
        timeSegList.add(nowEvent.getWhen());
      }

      /* an alternative to line 39-48
      if (!eventAttendees.disjoint(requestAttendees) {
        timeSegList.add(nowEvent.getWhen());
      }
      */
    }

    // sort the time ranges by their start time
    Collections.sort(timeSegList, TimeRange.ORDER_BY_START);

    // lastUncover: point to the latest available time
    int lastUncover = TimeRange.START_OF_DAY;
    for (TimeRange nowSeg: timeSegList) {
            
      // if there's a valid time range, add into answer
      if (nowSeg.start() > lastUncover && nowSeg.start() - lastUncover >= request.getDuration()) {
        answer.add(TimeRange.fromStartEnd(lastUncover, nowSeg.start(), false));
      }

      // update lastUncover
      if (nowSeg.end() > lastUncover) {
         lastUncover = nowSeg.end();
      }
    }

    // add the last time range
    if (TimeRange.END_OF_DAY - lastUncover + 1 >= request.getDuration()) {
      answer.add(TimeRange.fromStartEnd(lastUncover, TimeRange.END_OF_DAY, true));
    }

    return answer;
  }
}
