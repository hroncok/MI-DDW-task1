from django import forms

class URLForm(forms.Form):
    url = forms.CharField(label='URL of the web page', max_length=200)
