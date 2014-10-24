function searchValidation(){

  var searchWord1=document.quicksearch.searchWord1.value;
  searchWord1 = searchWord1.replace(/(^\s+|\s+$)/g,""); // left|right-trim

  if((searchWord1==null) || (searchWord1.length == 0))
  {
    window.alert("Enter at least one term to search in the database.");
    document.quicksearch.searchWord1.value = searchWord1;
    document.quicksearch.searchWord1.focus();
    return false;
  }

  if(searchWord1.match(/^\?$/)) {
    document.quicksearch.searchWord1.value = searchWord1;
    document.quicksearch.searchWord1.focus();
    window.alert("Search term must contain more than a wildcard.");
    return false;
  }

  if(searchWord1.match(/(^\*|\*$)/)) {
    if(!searchWord1.match(/(\*\w+|\w+\*)/)) {
      document.quicksearch.searchWord1.value = searchWord1;
      document.quicksearch.searchWord1.focus();
      window.alert("Search term must contain more than a wildcard.");
      return false;
    }
    searchWord1 = searchWord1.replace(/(^\*{2,}|\*{2,}$)/g,"*");
  }

  if(typeof(document.quicksearch.allcol) != 'undefined')
  {
    if(!document.quicksearch.allcol.checked)
    {
      var onechecked = false;
      for(i = 0; i < document.quicksearch.col.length; i++)
      {
        onechecked = onechecked || document.quicksearch.col[i];
      }
      if(!onechecked)
      {
        document.quicksearch.searchWord1.value = searchWord1;
        document.quicksearch.allcol.focus();
        window.alert("Please select a collection");
        return false;
      }
    }
  }

  document.quicksearch.searchWord1.value = searchWord1;
  return true;
}

function change(coll)
{
    if(coll == 'all')
    {
      // uncheck all individual collections
      // when all collections is selected
      var i = 0;
      for(i = 0; i < document.quicksearch.col.length; i++)
      {
        document.quicksearch.col[i].checked = false;
      }
    }
    else
    {
      // otherrwise uncheck the all collections when an individual
      // collection is selected
      document.quicksearch.allcol.checked = false;
    }

    return true;
}
