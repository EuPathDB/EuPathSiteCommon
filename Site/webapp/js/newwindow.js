/**
 *  @deprecated Use the class 'open-window'. 
 * 
 *  Features can be configured using data attributes. To get the functionality below,
 *  write the following:
 *    <a href="someurl" class="new-window" data-resizable="yes" data-scrollbars="yes" data-height="600" data-width="800">Go to new window</a>
 */
var newwindow;
function poptastic(url,name,h,w)
{
if(h) {
	newwindow=window.open(url,name,'resizable=yes,scrollbars=yes,height='+h+',width='+w);
	if (window.focus) {newwindow.focus()}
} else {
	newwindow=window.open(url,name,'resizable=yes,scrollbars=yes,height=600,width=800');
	if (window.focus) {newwindow.focus()}
     }
}
