/*
  Date Range Picker Widget v0.2
  
  Created by Sykes,rduyckin on 2008-01-10.
  Copyright (c) 2006 Media~Hive Inc.. All rights reserved.

***************************************************************************/


dojo.provide("atg.csc.dateRangePicker");

dojo.declare(
  "atg.csc.dateRangePicker",
  [dijit._Widget, dijit._Templated, dijit._Container],
  {
    widgetsInTemplate: true,
    ppData: '',
        templatePath: atg.commerce.csr.getContextRoot() + "/script/widget/templates/dateRangePicker.jsp",
    //templatePath: "../dijit/templates/dateRangePicker.jsp",        
    parentForm: {},
    
    _scheduleType: "Interval",        // 'Interval' | 'Calendar'
    _intervalOption: "days",            // 'days' | 'weeks'
    _interval: 1,                   // 1+   sinlge numeric value  
    _daysOption: "allDays",              // 'selectedDays' | 'selectedDates' | 'allDays'
    _weeksOption: "allOccurrences",              // 'allOccurrences'| 'selectedOccurrences' 
    _occurrences: new Array(),                      // 1-5   5 meaning the last week
    _days: new Array(),                       // 1-7  multi select   comma delim list i.e 1,2,3,4,5,6,7
    _dates: new Array(),                       // 1-31  multi select  comma delim list i.e 1,2,3,4,5,6...
    _monthsOption: "allMonths",      // 'allMonths' | 'selectedMonths'
    _months: new Array(),                     // 0-11 multi select   comma delim list i.e 0,1,2,3,4,5,6...
    _daysSelectHandle: '',
    _datesSelectHandle: '',
    _monthsSelectHandle: '',
    _weeksSelectHandle: '',            
    
    postCreate: function(){
      this._daysSelectHandle = dojo.subscribe("atg/csc/scheduleorder/daysSelect", this ,"onWeekDays" );
      this._datesSelectHandle = dojo.subscribe("atg/csc/scheduleorder/datesSelect", this ,"onDates" );            
      this._monthsSelectHandle = dojo.subscribe("atg/csc/scheduleorder/monthsSelect", this ,"onMonths" );                        
      this._weeksSelectHandle = dojo.subscribe("atg/csc/scheduleorder/weeksSelect", this ,"onOccurrences" );
      this.inherited("postCreate", arguments);
      dojo.byId('atg_dateRangePicker_intervalTab').style.position= "static";
      dojo.byId('atg_dateRangePicker_calendarTab').style.position= "static";        
    },
    
    destroy: function(){
      dojo.unsubscribe(this._daysSelectHandle );
      dojo.unsubscribe(this._datesSelectHandle );            
      dojo.unsubscribe(this._monthsSelectHandle );                        
      dojo.unsubscribe(this._weeksSelectHandle);
    },
        
    toggleHandler: function(e){
        console.debug("toggleHandler: " + e)
    },
        
    scheduleType: function(newValue){            

            if(newValue != null){                
            console.debug("SET scheduleType",newValue );
                this._scheduleType = newValue;    
            }else{
            //console.debug("GET scheduleType",this._scheduleType + ":" + dijit.byId("intervalTab").selected );  
            if (dijit.byId("atg_dateRangePicker_intervalTab").selected){
                return "Interval";
            }else{
                return "Calendar";
            }
                return this._scheduleType;
            }            
        },

    intervalOption: function(newValue){            
            if(newValue != null){                
            console.debug("SET intervalOption",newValue );
                this._intervalOption = newValue;    
            }else{
            //console.debug("GET intervalType",this._intervalType );                
                return this._intervalOption;
            }            
        },

    interval: function(newValue){            
            if(newValue != null){                
            console.debug("SET interval",newValue );
                this._interval = newValue;    
            }else{
            //console.debug("GET interval",this._interval );                
                return this._interval;
            }            
        },

    daysOption: function(newValue){            
            if(newValue != null){                
            console.debug("SET daysOption", newValue );
                this._daysOption = newValue;    
            }else{
            //console.debug("GET daysOption", this._daysOption );                
                return this._daysOption;
            }            
        },

    weeksOption: function(newValue){            
            if(newValue != null){                
            console.debug("SET weeksOption", newValue );
                this._weeksOption = newValue;    
            }else{
            //console.debug("GET weeksOption", this._weeksOption );                
                return this._weeksOption;
            }            
        },

        
        // ARRAY        
        occurrences: function(newValue){            
            if(newValue != null){                                
            console.debug("SET occurrences", newValue );
                this._occurrences = newValue;    
            }else{        
                return this.parseIntArray(this._occurrences);
            }            
        },

        // ARRAY    
    days: function(newValue){            
            if(newValue != null){                
            console.debug("SET days", newValue );
                this._days = newValue;    
            }else{
            //console.debug("GET days", this._days );                
                return this.parseIntArray(this._days);
            }            
        },

        // ARRAY
        dates: function(newValue){            
            if(newValue != null){                
            console.debug("SET dates",newValue );
                this._dates = newValue;    
            }else{
            //console.debug("GET dates",this._dates + ":" + this._dates.length );                
            //console.debug("GET cleaned dates",this.parseIntArray(this._dates) + ":" + this._dates.length)
                return this.parseIntArray(this._dates);
            }            
        },
    
    monthsOption: function(newValue){            

            if(newValue != null){                
            console.debug("SET monthsOption",newValue );
                this._monthsOption = newValue;    
            }else{
           // console.debug("GET monthsOption",this._monthsOption );                
                return this._monthsOption;
            }            
        },

    // ARRAY    
    months: function(newValue){            
      if(newValue != null){                
        console.debug("SET months",newValue );
        this._months = newValue;    
      }else{
        // console.debug("GET months",this._months );                
        return this.parseIntArray(this._months);
      }           
    },
    
    parseIntArray: function(items){
      //make sure the values are numeric       
      // check for an empty array
      var outputArray = new Array();
      var item = 0;
      for(i = 0 ;i < items.length; i++){
        item = items[i] + "";        
        //remove NaN elements
        if((! isNaN(item) && (item != "") )){
          outputArray.push(parseInt(item));
        }
      }

      return outputArray;
    },    
    
    onSelectChild: function(obj){
      console.debug("onSelectChild");
      console.debug(obj.id);
      this.selectedTab = obj.id;
      
      //dojo.byId("atg_schedOrder_interval").checked = true;
      
      if(this.selectedTab == "intervalTab"){
          dojo.byId("atg_schedOrder_interval").checked = true;
          this.scheduleType('Interval');
      }else{
           dojo.byId("atg_schedOrder_calendar").checked = true;
          this.scheduleType('Calendar');           
      }       
      
      
    },
    
    onSelectChange: function(e){
      console.debug("TARGET:" + e.target.value );
      // find all the expanded areas and 
      var radioGroupName = e.target.name;
            
      dojo.query('.expandedContent').forEach(function(thisItem){
        
        var radioGroup = dojo.query('input[name=' + radioGroupName + ']', thisItem.parentNode);
      
        if(radioGroup.length!=0){
          // Hide the expanded area
          thisItem.style.display = "none";  
          // Reset the selections in that area (is this actually desirable?)
          // TODO: this is specific to multi-selects, need to make it work with full UI.    
          dojo.query('select', thisItem).forEach(function(select){
            select.selectedIndex = -1;
          })
        }  
      });
            
      // set the values
      switch (e.target.value){
          case 'everyDay':
              this.daysOption('allDays');
              break;
          case 'selectDay':
              this.daysOption('selectedDays');
              break;
          case 'selectDate':
              this.daysOption('selectedDates');
              break;
          case 'everyMonth':
              this.monthsOption('allMonths');              
              break;
          case 'selectMonth':
              this.monthsOption('selectedMonths');
              break;
          default:
              break;
      }
                

      // todo: reset the select box of now closed areas
      
      // find sibling expanded  area for the radio that was just clicked and 
      // expand the exapnded area for that event
      
      dojo.query('.expandedContent', e.target.parentNode.parentNode).forEach(function(thisItem){
        thisItem.style.display = "block";        
      });
      
      
    },
    
    render: function(e){
        console.debug("dateRangePicker.render()" + this.scheduleType());
        // schedule type
        if(this._scheduleType == 'Interval'){
                dijit.byId("atg_dateRangePicker_intervalTab").selected = true;
                dijit.byId("atg_dateRangePicker_calendarTab").selected = false;
                dijit.byId("atg_dateRangePickerTabs").selectChild(dijit.byId("atg_dateRangePicker_intervalTab"));                
            this.selectedTab = "atg_schedOrder_interval";            
        }else{
                dijit.byId("atg_dateRangePicker_intervalTab").selected = false;
                dijit.byId("atg_dateRangePicker_calendarTab").selected = true;
                dijit.byId("atg_dateRangePickerTabs").selectChild(dijit.byId("atg_dateRangePicker_calendarTab"));
            this.selectedTab = "atg_schedOrder_calendar";            
        }
        // interval
        console.debug("render: update interval.value")
        this.intervalValue.value = this.interval();
        //interval type
            var options = this.intervalSelect.options;
            for(i = 0 ; i<options.length ; i++){
            //console.debug("option:" + options[i].value + ":" + this.intervalOption());
                if(options[i].value == this.intervalOption()){
                    options[i].selected = true;
                }
            }

            
            //  days option
            if(this.daysOption() == "allDays"){
                this.everyDayOption.checked=true;
                this.selectDaysPanel.style.display = "none";
                this.selectDatesPanel.style.display = "none";
                console.debug("allDays");
            }else if(this.daysOption() == "selectedDays"){
                this.selectDaysOption.checked=true;
                this.selectDaysPanel.style.display = "block";
                this.selectDatesPanel.style.display = "none";
                console.debug("selectedDays");               
                 
            }else if(this.daysOption() == "selectedDates"){
                this.selectDatesOption.checked=true;
                this.selectDaysPanel.style.display = "none";
                this.selectDatesPanel.style.display = "block";
                console.debug("selectedDates");                
            }
            
            // occurrences option
            
            if(this.weeksOption() == "allOccurrences"){
               this.allOccurrencesOption.checked = true;
               this.selectedOccurrencesOption.checked = false;
               this.weeksSelectPanel.style.display = "none";
            }else{
            
                this.allOccurrencesOption.checked = false;
               this.weeksSelectPanel.style.display = "block";
                this.selectedOccurrencesOption.checked = true;
            }

             // months option             
             if(this.monthsOption() == "allMonths"){
                  this.allMonthsOption.checked = true;
                  this.selectedMonthsOption.checked = false;
                  console.debug("hiding monthsSelect");
                  this.monthsSelectPanel.style.display = "none";
              }else{
                  this.allMonthsOption.checked = false;
                  this.selectedMonthsOption.checked = true;
                console.debug("showing monthsSelect");
                  this.monthsSelectPanel.style.display = "block";
              }
              

            // find all of the toggles and render any that have valid values set
            var registry = dijit.registry;

            for(var id in registry._hash){
                var item = registry._hash[id];
               if(item.declaredClass =="atg.csc.toggleLink"){
                   var togglesArray = new Array();

                   switch(item.type){
                       case "month":
                        var togglesArray = this.months();
                        break;
                        
                       case "day":
                        var togglesArray = this.days();
                        break;
                        
                       case "week":
                        var togglesArray = this.occurrences();
                        break;
                        
                       case "date":
                        var togglesArray = this.dates();
                        break;

                       default:
                        break;
                   }
                   for(j = 0 ; j<togglesArray.length ; j++){
                      if(togglesArray[j] == item.value){
                          item.show();
                      }
                    }
                   
                   
               }
           }

    },
    
    onIntervalOption: function(e){
      console.debug("Interval Option:" + e.target.value);
      options = this.intervalSelect;

      for (i = 0 ;i<options.length;i++){
        if(options[i].selected == true){
          this.intervalOption(options[i].value);
        }
      }
      console.debug("intervalOption:" + this.intervalOption());         
    },
    
    onOccurrences: function(item){
      console.debug("Occurrence:" + item.value);
      if(item.toggleState == 1){
        this._occurrences[this._occurrences.length] = item.value;
      }else{
        this._occurrences = this.occurrences();
        //ie6 workaround        
        var occurrencePosition = this.getItemIndex(this.occurrences(),item.value);
        this._occurrences.splice(occurrencePosition,1);
      }
    },

    onWeekDays: function(item){      
      console.debug("onWeekDays: " + item.label + " : " + this.days() );
      if(item.toggleState == 1){       
        this._days[this._days.length] = item.value;
      }else{       
        this._days = this.days();        
        //ie6 workaround        
        var weekdayPosition = this.getItemIndex(this.days(),item.value);
        this._days.splice(weekdayPosition,1);
      }
      console.debug("days array: " + this.days());
    },
    
    onDay: function(e){
        console.debug("WeekDay:" + e.target.id);
    },
    
    onDates: function(item){
      console.debug("Day:" + item.value);
      if(item.toggleState == 1){
        this._dates[this._dates.length] = item.value;
      }else{
        this._dates = this.dates();
        //ie6 workaround        
        var datePosition = this.getItemIndex(this.dates(),item.value);
        this._dates.splice(datePosition,1);
      }
      console.debug("dates:" + this.dates().join(","));
    },
    
    onMonths: function(item){
      console.debug("month:" + item.value);
      if(item.toggleState == 1){
        this._months[this._months.length] = item.value;
      }else{
        this._months = this.months();
        //ie6 workaround        
        var monthPosition = this.getItemIndex(this.months(),item.value);
        this._months.splice(monthPosition,1);
      }
      console.debug("months:" + this.months().join(","));
    },
    
        
    onWeekSelect: function(e){
        var radioGroupName = e.target.name;
        this.weeksOption('selectedOccurrences');            
        dojo.query('.atg_dateRangePicker_weeksSelect', e.target.parentNode.parentNode).forEach(function(thisItem){
        thisItem.style.display = "block";        
      });
    },

    onAllWeeksSelect: function(e){
      var radioGroupName = e.target.name;
      dojo.byId("weekListing").style.display = "none" ;

      this.weeksOption('allOccurrences');
    },
    
    onScheduleType: function(e){
        
      if(dojo.byId("atg_schedOrder_interval").checked){
          this.scheduleType("Interval");
      }else{
          this.scheduleType("Calendar");
      }
    },
    
    onInterval: function(e){
        console.debug(this.intervalValue.value);
        // only allow numeric 
        var tempValue = this.intervalValue.value;
        var newValue = "";
        for(i = 0 ;i < tempValue.length ;i++){
          if(! isNaN(tempValue.charAt(i))){
            newValue += tempValue.charAt(i);
          }          
        }
        this.intervalValue.value = newValue;
        this.interval(tempValue);
    },
    
    getItemIndex: function(list, obj){      
        for(var i=0; i<list.length; i++){
          if(list[i] == obj){
            return i;
          }
        }
        return -1;
    },
    
    hijackForm: function(){
      
      this.parentForm = this.getParentOfType(this.domNode, ["FORM"]);
      
      this.formSubmitButton = dojo.query('input[type="submit"]', this.parentForm)['0'];
      
      dojo.connect(this.formSubmitButton, "onclick", this, "submitClicked");
      
      
    },
    
    
    submitClicked: function(e){
      
      //console.debug(this.parentForm);
      //e.preventDefault();
      
    },
    
    
    // Utility Functions
    /* from the Dojo 1.0 editor, no idea why it's not in core */
    
    isTag: function(/*DomNode*/node, /*Array*/tags){
      if(node && node.tagName){
        var _nlc = node.tagName.toLowerCase();
        for(var i=0; i<tags.length; i++){
          var _tlc = String(tags[i]).toLowerCase();
          if(_nlc == _tlc){
            return _tlc;
          }
        }
      }
      return "";
    },
    
    getParentOfType: function(/*DomNode*/node, /*Array*/tags){
      while(node){
        if(this.isTag(node, tags).length){
          return node;
        }
        node = node.parentNode;
      }
      return null;
    },
    
    
    sanitySaver: ''
});
