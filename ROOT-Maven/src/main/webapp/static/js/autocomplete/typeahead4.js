/* =============================================================
 * bootstrap-typeahead.js v2.2.1
 * http://twitter.github.com/bootstrap/javascript.html#typeahead
 * =============================================================
 * Copyright 2012 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ */


(function (factory) {
  if (typeof define === 'function' && define.amd) {
    define(['jquery'], factory)
  } else {
    factory(jQuery)
  }
}(function ($) {

  "use strict"; // jshint ;_;


 /* TYPEAHEAD PUBLIC CLASS DEFINITION
  * ================================= */

  var Typeahead = function (element, options) {
    this.$element = $(element)
    this.options = $.extend({}, $.fn.typeahead.defaults, options)
    this.matcher = this.options.matcher || this.matcher
    this.sorter = this.options.sorter || this.sorter
    this.highlighter = this.options.highlighter || this.highlighter
    this.updater = this.options.updater || this.updater
    this.$menu = $(this.options.menu).appendTo('body')
    this.source = this.options.source
    this.shown = false
    this.listen()
    $("#submenu").delegate('li','click',function(e){
      e.preventDefault();
      e.stopPropagation();
      $("#srchWrd1").val($(this).text());
      $(element).trigger('hide');
      $("#submenu").hide();
    }).delegate('li','mouseenter',function(){
      $(this).closest('li').addClass("active");
    }).delegate('li','mouseleave',function(){
      $(this).closest('li').removeClass("active");
    });
    $("#autofooter a").on('click',$.proxy(function(e){
      e.preventDefault();
      this.$menu.hide();
      $("#submenu").hide();
      this.$element.val("").focus();
    },this));
    $(document).click(function(){$("#submenu").hide()});
  }

  Typeahead.prototype = {

    constructor: Typeahead

  , select: function () {
      var val = this.$menu.find('.active').attr('data-value');
      
      //if this is a 'thesaurus element
      //then show all found search terms underneath it
      if(/\.\.\.$/.test(val)){
        // if already selected, then close
        if (this.$menu.find('.thesaurus_icon').hasClass('selected')) {
            this.$menu.find('.thesaurus_icon').removeClass('selected');
            this.$menu.find('.sub').remove();
            return
        }
        // else open up with search terms underneath
        this.$menu.find('.active').find('.thesaurus_icon').addClass('selected');
        // find the thesarus listings for this term
        var arrys = searchdata[val.replace('...','')].full;
        var liStr = "";
        // create list of terms and add them to list after thesaurus term
        // Note: fOR IE8/IE7 -- concat and forEach not supported...
        arrys[0].concat(arrys[1]).forEach(function(item){
          liStr += "<li class=\"sub\" data-value=\""+item+"\"><a href='#'>"+item + "</a></li>";
        });
        var liElement = $(liStr);
        this.$menu.find('.active').after(liElement);
        return
      }
      this.$element
        .val(this.updater(val))
        .change()
      return this.hide()
    }

  , updater: function (item) {
      return item
    }

  , show: function () {
      var pos = $.extend({}, this.$element.offset(), {
        height: this.$element[0].offsetHeight
      })

      this.$menu.css({
        top: pos.top + pos.height
      , left: pos.left
      })

      this.$menu.show()
      this.shown = true
      this.$element.trigger('show');
      return this
    }

  , hide: function () {
      this.$menu.hide()
      this.shown = false
      this.$element.trigger('hide');
      return this
    }

  , lookup: function (event) {
      var items

      this.query = this.$element.val()

      if (!this.query || this.query.length < this.options.minLength) {
        return this.shown ? this.hide() : this
      }

      items = $.isFunction(this.source) ? this.source(this.query, $.proxy(this.process, this)) : this.source
      return items ? this.process(items) : this
    }

  , process: function (items) {
      var that = this

      items = $.grep(items, function (item) {
        return that.matcher(item)
      })

      items = this.sorter(items)
      if (!items.length) {
        return this.shown ? this.hide() : this
      }

      this.render(items.slice(0, this.options.items)).show()
      var pos = $.extend({}, this.$menu.offset(), {
        height: this.$menu[0].offsetHeight
      })
      $("#autofooter").css({
        top: pos.top + pos.height - 3
      , left: pos.left
      })
      return
    }

  , matcher: function (item) {
      return ~item.toLowerCase().indexOf(this.query.toLowerCase())
    }

  , sorter: function (items) {
      var beginswith = []
        , caseSensitive = []
        , caseInsensitive = []
        , item

      while (item = items.shift()) {
        if (!item.toLowerCase().indexOf(this.query.toLowerCase())) beginswith.push(item)
        else if (~item.indexOf(this.query)) caseSensitive.push(item)
        else caseInsensitive.push(item)
      }

      return beginswith.concat(caseSensitive, caseInsensitive)
    }

  , highlighter: function (item) {
      var query = this.query.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g, '\\$&')
      query = '<div class="searchTerm">' + query + '</div>';
      return item.replace(new RegExp('(' + query + ')', 'ig'), function ($1, match) {
        return '<strong>' + match + '</strong>'
      })
    }

  , render: function (items) {
//    debugger;
      var that = this

      items = $(items).map(function (i, item) {
        i = $(that.options.item).attr('data-value', item)
        i.find('a').html(that.highlighter(item))
        return i[0]
      })

      items.first().addClass('active')
      this.$menu.html(items)
      return this
    }

  , next: function (event) {
      var active = this.$menu.find('.active').removeClass('active')
        , next = active.next()

      if (!next.length) {
        next = $(this.$menu.find('li')[0])
      }

      return next.addClass('active')
    }

  , prev: function (event) {
      var active = this.$menu.find('.active').removeClass('active')
        , prev = active.prev()

      if (!prev.length) {
        prev = this.$menu.find('li').last()
      }

      return prev.addClass('active')
    }

  , listen: function () {
      this.$element
        .on('blur',     $.proxy(this.blur, this))
        .on('keypress', $.proxy(this.keypress, this))
        .on('keyup',    $.proxy(this.keyup, this))
        .on('show',  function(){$("#autofooter").show()}) 
        .on('hide',  function(){$("#autofooter").hide()}) 

      if (this.eventSupported('keydown')) {
        this.$element.on('keydown', $.proxy(this.keydown, this))
      }

      this.$menu
        .on('click', $.proxy(this.click, this))
        .on('mouseenter', 'li', $.proxy(this.mouseenter, this))
      
      $(document).mousemove($.proxy(function(e){
        this.pageX = e.pageX;
        this.pageY = e.pageY;
      },this));
    }

  , eventSupported: function(eventName) {
      var isSupported = eventName in this.$element
      if (!isSupported) {
        this.$element.setAttribute(eventName, 'return;')
        isSupported = typeof this.$element[eventName] === 'function'
      }
      return isSupported
    }

  , move: function (e) {
      if (!this.shown) return
      var item;

      switch(e.keyCode) {
        case 9: // tab
        case 13: // enter
        case 27: // escape
          e.preventDefault()
          break

        case 38: // up arrow
          e.preventDefault()
          item = this.prev()
          break

        case 40: // down arrow
          e.preventDefault()
          item = this.next()
          break
      }

      e.stopPropagation()
    }

  , keydown: function (e) {
      this.suppressKeyPressRepeat = ~$.inArray(e.keyCode, [40,38,9,13,27])
      this.move(e)
    }

  , keypress: function (e) {
      if (this.suppressKeyPressRepeat) return
      this.move(e)
    }

  , keyup: function (e) {
      switch(e.keyCode) {
        case 40: // down arrow
        case 38: // up arrow
        case 16: // shift
        case 17: // ctrl
        case 18: // alt
          break

        case 9: // tab
        case 13: // enter
          if (!this.shown) return
          this.select()
          break

        case 27: // escape
          if (!this.shown) return
          this.hide()
          break

        default:
          this.lookup()
      }
      $("#submenu").hide();

      e.stopPropagation()
      e.preventDefault()
  }

  , blur: function (e) {
      var that = this
      var boxleft = this.$menu.width()+this.$menu.position().left;
return;
      if(Math.abs(this.pageX - boxleft) > 50)
        setTimeout(function () { that.hide() }, 150)
      else
        setTimeout(function () { that.$element.focus() }, 150)
    }

  , click: function (e) {
      e.stopPropagation()
      e.preventDefault()
      this.select()
    }

  , mouseenter: function (e) {
      this.$menu.find('.active').removeClass('active')
      $(e.currentTarget).addClass('active')
    }

  }


  /* TYPEAHEAD PLUGIN DEFINITION
   * =========================== */

  $.fn.typeahead = function (option) {
    return this.each(function () {
      var $this = $(this)
        , data = $this.data('typeahead')
        , options = typeof option == 'object' && option
      if (!data){
        data = [];
        data.push(new Typeahead(this,options));
      }else{
        data.push(new Typeahead(this,options));
      }
      $this.data('typeahead', data);
      //if (typeof option == 'string')
      //  data[option]()
    })
  }

  $.fn.typeahead.defaults = {
    source: []
  , items: 8
  , menu: '<ul class="typeahead dropdown-menu"></ul>'
  , item: '<li><a href="#"></a></li>'
  , minLength: 1
  }

  $.fn.typeahead.Constructor = Typeahead


 /*   TYPEAHEAD DATA-API
  * ================== */

  $(document).on('focus.typeahead.data-api', '[data-provide="typeahead"]', function (e) {
    var $this = $(this)
    if ($this.data('typeahead')) return
    e.preventDefault()
    $this.typeahead($this.data())
  })

}));
